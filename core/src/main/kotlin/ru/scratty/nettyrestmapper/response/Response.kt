package ru.scratty.nettyrestmapper.response

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

open class Response private constructor(
    private val httpResponseStatus: HttpResponseStatus,
    private val content: ByteBuf
) {

    constructor(responseStatus: ResponseStatus, content: ByteBuf): this(responseStatus.httpResponseStatus, content)

    constructor(code: Int, content: ByteBuf): this(HttpResponseStatus.valueOf(code), content)

    constructor(responseStatus: ResponseStatus, content: ByteArray): this(responseStatus, Unpooled.copiedBuffer(content))

    constructor(code: Int, content: ByteArray): this(code, Unpooled.copiedBuffer(content))

    constructor(responseStatus: ResponseStatus, content: CharArray): this(responseStatus, Unpooled.copiedBuffer(content, StandardCharsets.UTF_8))

    constructor(code: Int, content: CharArray): this(code, Unpooled.copiedBuffer(content, StandardCharsets.UTF_8))

    constructor(responseStatus: ResponseStatus, content: String): this(responseStatus, content.toCharArray())

    constructor(code: Int, content: String): this(code, content.toCharArray())

    constructor(responseStatus: ResponseStatus): this(responseStatus, Unpooled.EMPTY_BUFFER)

    constructor(code: Int): this(code, Unpooled.EMPTY_BUFFER)

    var httpVersion: HttpVersion = HttpVersion.HTTP_1_1

    var contentType = "text/plane"
    var acceptCharset: Charset = StandardCharsets.UTF_8

    fun createFullHttpResponse(): FullHttpResponse = DefaultFullHttpResponse(httpVersion, httpResponseStatus, content).apply {
        with(headers()) {
            set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
            set(HttpHeaderNames.CONTENT_TYPE, contentType)
            set(HttpHeaderNames.CONTENT_LENGTH, content().readableBytes())
            set(HttpHeaderNames.ACCEPT_CHARSET, acceptCharset.name())
        }
    }
}