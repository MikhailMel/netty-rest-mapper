package ru.scratty.nettyrestmapper

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.annotation.*
import ru.scratty.nettyrestmapper.exception.NumAnnotationsException
import ru.scratty.nettyrestmapper.exception.ParameterException
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

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

                val pathParamsNames = URL_PARAMS_REGEX.findAll(path)
                    .map { it.groupValues[1] }
                    .toList()
                val pathParamsNamesInMethod = arrayListOf<String>()

                val parameters = arrayListOf<MethodParameter>()

                for (parameter in function.parameters) {
                    val pathParamAnnotation = parameter.findAnnotation<PathParam>() ?: continue
                    val pathParamName = if (pathParamAnnotation.name.isNotEmpty()) {
                        pathParamAnnotation.name
                    } else {
                        parameter.name ?: ""
                    }
                    pathParamsNamesInMethod.add(pathParamName)

                    if (!pathParamsNames.contains(pathParamName)) {
                        throw ParameterException("Parameter '$pathParamName' isn't specified in the path '$path'")
                    }

                    parameters.add(MethodParameter(pathParamName, parameter.type.jvmErasure.java))
                }

                for (pathParam in pathParamsNames) {
                    if (!pathParamsNamesInMethod.contains(pathParam)) {
                        throw ParameterException("Parameter '$pathParam' isn't specified in the function '${function.name}' with path '$path'")
                    }
                }

                function.isAccessible = true
                val methodHandler = HttpMethodHandler(
                    httpMethod,
                    path,
                    pathParamsNames,
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