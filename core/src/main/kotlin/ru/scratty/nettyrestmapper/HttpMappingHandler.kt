package ru.scratty.nettyrestmapper

import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.http.*
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.exception.FewMethodsHandleException


class HttpMappingHandler(
    private val httpMethodsHandlers: List<HttpMethodHandler>
) : ChannelInboundHandlerAdapter() {

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
            matchedMethodsHandlers.isEmpty() -> DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND)
            else -> {
                val methodHandler = matchedMethodsHandlers[0]
                methodHandler.invoke(emptyArray<Any>()).createFullHttpResponse()
            }
        }

        val channelFuture = ctx.writeAndFlush(response)
        channelFuture.addListener(ChannelFutureListener.CLOSE)

        request.release()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error(cause.message, cause)

        val channelFuture = ctx.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR))
        channelFuture.addListener(ChannelFutureListener.CLOSE)
    }
}