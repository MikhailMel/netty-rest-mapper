package ru.scratty.nettyrestmapper

import com.google.gson.Gson
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.exception.FewMethodsHandleException
import ru.scratty.nettyrestmapper.exception.ParameterException
import ru.scratty.nettyrestmapper.exception.ParameterMissingException
import ru.scratty.nettyrestmapper.parameter.FunctionParameter
import ru.scratty.nettyrestmapper.response.Response
import ru.scratty.nettyrestmapper.response.ResponseStatus
import java.nio.charset.StandardCharsets


class HttpMappingHandler(
    private val httpMethodsHandlers: List<HttpMethodHandler>
) : ChannelInboundHandlerAdapter() {

    private val gson = Gson()

    val defaultParameterParser = fun(parameter: FunctionParameter, value: String): Any? {
        return gson.fromJson(value, parameter.variableType)
    }

    companion object {
        private val log = LoggerFactory.getLogger(HttpMappingHandler::class.java)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is HttpRequest) {
            return
        }

        val request = msg as FullHttpRequest
        log.debug("New request ${request.method().name()} '${request.uri()}'")

        val matchedMethodsHandlers = httpMethodsHandlers.filter {
            it.isPathMatched(request.uri()) && it.httpMethod == request.method()
        }
        log.debug("Matched method handlers found: ${matchedMethodsHandlers.size}")

        val response: FullHttpResponse = when {
            matchedMethodsHandlers.size > 1 -> throw FewMethodsHandleException(request.method().name(), request.uri())
            matchedMethodsHandlers.isEmpty() -> DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND
            )
            else -> {
                try {
                    val methodHandler = matchedMethodsHandlers[0]
                    methodHandler
                        .invoke(getArgsForInvoke(methodHandler, request))
                        .createFullHttpResponse()
                } catch (parameterMissing: ParameterMissingException) {
                    Response(ResponseStatus.BAD_REQUEST, parameterMissing.message!!)
                        .createFullHttpResponse()
                }
            }
        }

        val channelFuture = ctx.writeAndFlush(response)
        channelFuture.addListener(ChannelFutureListener.CLOSE)

        request.release()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error(cause.message, cause)

        val channelFuture =
            ctx.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        channelFuture.addListener(ChannelFutureListener.CLOSE)
    }

    private fun getArgsForInvoke(methodHandler: HttpMethodHandler, request: FullHttpRequest): Array<Any?>? {
        val queryStringDecoder = QueryStringDecoder(request.uri())
        val postRequestDecoder = HttpPostRequestDecoder(request)

        val pathParameterValues = hashMapOf<String, String>()
        val matchedGroupValues = methodHandler.pathPattern.findAll(request.uri())
            .flatMap { it.groupValues.toList().asSequence() }
            .toList()

        methodHandler.pathParams.forEachIndexed { i, v ->
            pathParameterValues[v] = matchedGroupValues[i + 1]
        }

        val parameters = arrayOfNulls<Any?>(methodHandler.parameters.size)

        methodHandler.parameters.forEachIndexed { i, parameter ->
            val value = if (parameter.parameterType == FunctionParameter.ParamType.PATH_PARAM) {
                pathParameterValues[parameter.name]
            } else if (parameter.parameterType == FunctionParameter.ParamType.QUERY_PARAM) {
                val bodyParameter = postRequestDecoder.getBodyHttpData(parameter.name)
                val list = queryStringDecoder.parameters()[parameter.name]

                if (bodyParameter != null && !list.isNullOrEmpty()) {
                    throw ParameterException("The '${parameter.name}' parameter is present in the body and in the request URI, path '${request.uri()}'")
                } else if (!list.isNullOrEmpty()) {
                    list[0]
                } else if (bodyParameter != null) {
                    (bodyParameter as Attribute).value
                } else {
                    parameter.default
                }
            } else {
                val content = request.content().toString(StandardCharsets.UTF_8)

                if (content.isNullOrEmpty()) {
                    parameter.default
                } else {
                    content
                }
            }

            if (value != null && value.isNotEmpty()) {
                parameters[i] = defaultParameterParser(parameter, value)
            } else if (parameter.required) {
                throw ParameterMissingException(parameter)
            }
        }

        log.debug(parameters.toList().toString())

        return parameters
    }
}