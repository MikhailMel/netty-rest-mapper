package ru.scratty.nettyrestmapper

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.HttpRequest
import ru.scratty.nettyrestmapper.exception.FewMethodsHandlePath

class HttpMappingHandler(
    private val httpMethodsHandlers: List<HttpMethodHandler>
) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg !is HttpRequest) {
            return
        }

        val request = msg as HttpRequest

        val matchedMethodsHandlers = httpMethodsHandlers.filter {
            it.isPathMatched(request.uri()) && it.httpMethod == request.method()
        }

        if (matchedMethodsHandlers.size > 1) {
            throw FewMethodsHandlePath(request.uri())
        } else if (matchedMethodsHandlers.isEmpty()) {
            //TODO
        }

        val methodHandler = matchedMethodsHandlers[0]
        methodHandler.invoke(emptyArray<Any>())
    }
}