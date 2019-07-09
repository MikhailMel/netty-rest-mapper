package ru.scratty.nettyrestmapper

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.annotation.*
import ru.scratty.nettyrestmapper.exception.NumAnnotationsException
import ru.scratty.nettyrestmapper.exception.ParameterException
import java.lang.reflect.Method
import kotlin.reflect.KParameter

class ControllerHandler(
    private val controllers: List<Any>
) {

    companion object {
        private val ANNOTATIONS_MAP = mapOf(
            GetMapping::class.java to HttpMethod.GET,
            PostMapping::class.java to HttpMethod.POST,
            PutMapping::class.java to HttpMethod.PUT,
            PatchMapping::class.java to HttpMethod.PATCH,
            DeleteMapping::class.java to HttpMethod.DELETE
        )

        private val URL_PARAMS_REGEX = Regex("\\{(\\w*?)}")

        private val log = LoggerFactory.getLogger(ControllerHandler::class.java)
    }

    val httpMethodsHandlers: List<HttpMethodHandler> = createHttpMethodsHandlers()

    private fun createHttpMethodsHandlers(): List<HttpMethodHandler> {
        log.debug("Parsing controllers started (${controllers.size} controllers)")

        val list = arrayListOf<HttpMethodHandler>()

        for (controllerInstance in controllers) {
            val controller = controllerInstance.javaClass
            val restController = controller.getAnnotation(RestController::class.java) ?: continue
            val rootPath = restController.path

            for (method in controller.methods) {
                val annotation = findMappingAnnotation(method) ?: continue
                val httpMethod = ANNOTATIONS_MAP[annotation.annotationClass.java]
                    ?: throw Exception("Not found HttpMethod for '${annotation.javaClass.name}' annotation")

                val path = rootPath + annotation.javaClass.getMethod("path").invoke(annotation)

                val pathParamsNames = URL_PARAMS_REGEX.findAll(path)
                    .map { it.groupValues[1] }
                    .toList()
                val pathParamsNamesInMethod = arrayListOf<String>()

                val parameters = arrayListOf<MethodParameter>()

                for (parameter in method.parameters) {
                    val pathParamAnnotation = parameter.getAnnotation(PathParam::class.java)

                    pathParamsNamesInMethod.add(pathParamAnnotation.name)

                    if (!pathParamsNames.contains(pathParamAnnotation.name)) {
                        throw ParameterException("Parameter '${pathParamAnnotation.name}' isn't specified in the path '$path'")
                    }

                    parameters.add(MethodParameter(pathParamAnnotation.name, parameter.type))
                }

                for (pathParam in pathParamsNames) {
                    if (!pathParamsNamesInMethod.contains(pathParam)) {
                        throw ParameterException("Parameter '$pathParam' isn't specified in the method '${method.name}' with path '$path'")
                    }
                }

                method.isAccessible = true
                val methodHandler = HttpMethodHandler(
                    httpMethod,
                    path,
                    pathParamsNames,
                    parameters,
                    method,
                    controllerInstance
                )
                log.debug(methodHandler.toString())

                list.add(methodHandler)
            }
        }
        log.debug("Parsing controllers ended")

        return list
    }

    private fun findMappingAnnotation(method: Method): Annotation? {
        val annotations = ANNOTATIONS_MAP.keys
            .filter { method.isAnnotationPresent(it) }
            .map { method.getAnnotation(it) }

        return when (annotations.size) {
            1 -> annotations.first()
            0 -> null
            else -> throw NumAnnotationsException("Multiple annotations for method '${method.name}'")
        }
    }
}