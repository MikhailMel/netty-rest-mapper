package ru.scratty.nettyrestmapper.response

import io.netty.buffer.Unpooled

class OkResponse: Response {

    constructor(): super(ResponseStatus.OK)

    constructor(content: ByteArray): super(ResponseStatus.OK, Unpooled.copiedBuffer(content))

    constructor(content: String): super(ResponseStatus.OK, content.toByteArray())

    constructor(content: Byte): super(ResponseStatus.OK, content.toString())

    constructor(content: Short): super(ResponseStatus.OK, content.toString())

    constructor(content: Int): super(ResponseStatus.OK, content.toString())

    constructor(content: Long): super(ResponseStatus.OK, content.toString())

    constructor(content: Float): super(ResponseStatus.OK, content.toString())

    constructor(content: Double): super(ResponseStatus.OK, content.toString())

    constructor(content: Boolean): super(ResponseStatus.OK, content.toString())

    constructor(content: Collection<*>): super(ResponseStatus.OK, content.toString())
}