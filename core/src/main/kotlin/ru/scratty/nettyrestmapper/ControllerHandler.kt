package ru.scratty.nettyrestmapper

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.annotation.mapping.*
import ru.scratty.nettyrestmapper.annotation.parameter.PathParam
import ru.scratty.nettyrestmapper.annotation.parameter.QueryParam
import ru.scratty.nettyrestmapper.annotation.parameter.RequestBody
import ru.scratty.nettyrestmapper.exception.NumAnnotationsException
import ru.scratty.nettyrestmapper.exception.ParameterException
import ru.scratty.nettyrestmapper.parameter.FunctionParameter
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod

class ControllerHandler(
    private val controllers: List<Any>
) {

    companion object {
        private val ANNOTATIONS_MAP = mapOf(
            GetMapping::class to HttpMethod.GET,
            PostMapping::class to HttpMethod.POST,
            PutMapping::class to HttpMethod.PUT,
            PatchMapping::class to HttpMethod.PATCH,
            DeleteMapping::class to HttpMethod.DELETE
        )

        private val URL_PARAMS_REGEX = Regex("\\{(\\w*?)}")

        private val log = LoggerFactory.getLogger(ControllerHandler::class.java)
    }

    val httpMethodsHandlers: List<HttpMethodHandler> = createHttpMethodsHandlers()

    private fun createHttpMethodsHandlers(): List<HttpMethodHandler> {

        log.debug("Parsing controllers started (${controllers.size} controllers)")

        val list = arrayListOf<HttpMethodHandler>()

        for (controllerInstance in controllers) {
            val controller = controllerInstance::class

            val restController = controller.findAnnotation<RestController>() ?: continue
            val rootPath = restController.path

            for (function in controller.functions) {
                function.javaMethod ?: continue

                val annotation = findMappingAnnotation(function) ?: continue
                val httpMethod = ANNOTATIONS_MAP[annotation.annotationClass]
                    ?: throw Exception("Not found HttpMethod for '${annotation.javaClass.name}' annotation")

                val path = rootPath + annotation.javaClass.getMethod("path").invoke(annotation)
                val parameters = parseParameters(path, function, httpMethod)

                function.isAccessible = true
                val methodHandler = HttpMethodHandler(
                    httpMethod,
                    path,
                    parameters,
                    function.javaMethod!!,
                    controllerInstance
                )
                log.debug(methodHandler.toString())

                list.add(methodHandler)
            }
        }
        log.debug("Parsing controllers ended")

        return list
    }

    private fun parseParameters(path: String, function: KFunction<*>, httpMethod: HttpMethod): List<FunctionParameter> {
        val functionParameters = arrayListOf<FunctionParameter>()

        val pathParamNames = URL_PARAMS_REGEX.findAll(path)
            .map { it.groupValues[1] }
            .toList()
        val pathParamNamesInFunc = arrayListOf<String>()

        var requestBodyAnnotationsCounter = 0

        for (parameter in function.parameters) {
            if (parameter.kind != KParameter.Kind.VALUE) {
                continue
            }

            var annotationsCounter = 0
            var functionParameter = FunctionParameter(parameter)

            parsePathParamAnnotation(parameter)?.let {
                annotationsCounter++

                functionParameter = it
            }
            parseQueryParamAnnotation(parameter)?.let {
                annotationsCounter++

                functionParameter = it
            }
            parseRequestBodyAnnotation(parameter, httpMethod, path)?.let {
                annotationsCounter++
                requestBodyAnnotationsCounter++

                functionParameter = it
            }

            if (annotationsCounter == 0) {
                throw ParameterException(
                    "Missing annotation for parameter '${parameter.name}' of function '${function.name}' with path '$path'"
                )
            } else if (annotationsCounter > 1) {
                throw ParameterException(
                    "Multiple annotations for parameter '${parameter.name}' of function '${function.name}' with path '$path'"
                )
            }

            if (functionParameter.parameterType == FunctionParameter.ParamType.PATH_PARAM) {
                pathParamNamesInFunc.add(functionParameter.name)

                if (!pathParamNames.contains(functionParameter.name)) {
                    throw ParameterException("Parameter '${functionParameter.name}' isn't specified in the path '$path'")
                }
            }

            functionParameters.add(functionParameter)
        }

        for (pathParam in pathParamNames) {
            if (!pathParamNamesInFunc.contains(pathParam)) {
                throw ParameterException("Parameter '$pathParam' isn't specified in the function '${function.name}' with path '$path'")
            }
        }

        return functionParameters
    }

    private fun parsePathParamAnnotation(parameter: KParameter): FunctionParameter? =
        parameter.findAnnotation<PathParam>()?.let {
            FunctionParameter(
                parameter,
                parameter.parameterNameOrDefault(it.name),
                FunctionParameter.ParamType.PATH_PARAM
            )
        }

    private fun parseQueryParamAnnotation(parameter: KParameter): FunctionParameter? =
        parameter.findAnnotation<QueryParam>()?.let {
            FunctionParameter(
                parameter,
                parameter.parameterNameOrDefault(it.name),
                FunctionParameter.ParamType.QUERY_PARAM,
                it.required,
                it.default
            )
        }

    private fun parseRequestBodyAnnotation(
        parameter: KParameter,
        httpMethod: HttpMethod,
        path: String
    ): FunctionParameter? =
        parameter.findAnnotation<RequestBody>()?.let {
            if (httpMethod != HttpMethod.POST && httpMethod != HttpMethod.PUT && httpMethod != HttpMethod.PATCH) {
                throw ParameterException(
                    "RequestBody annotation can be used only in POST, PUT or PATCH requests, path '$path'"
                )
            }

            FunctionParameter(
                parameter,
                parameter.parameterNameOrDefault(""),
                FunctionParameter.ParamType.REQUEST_BODY,
                it.required,
                it.default
            )
        }

    private fun findMappingAnnotation(function: KFunction<*>): Annotation? {
        val annotations = function.annotations
            .filter { ANNOTATIONS_MAP.keys.contains(it.annotationClass) }

        return when (annotations.size) {
            1 -> annotations.first()
            0 -> null
            else -> throw NumAnnotationsException("Multiple annotations for function '${function.name}'")
        }
    }
}

fun KParameter.parameterNameOrDefault(default: String) = if (default.isEmpty()) this.name!! else default