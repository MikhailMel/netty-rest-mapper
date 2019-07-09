package ru.scratty.nettyrestmapper.response

class OkResponse(
    content: ByteArray
): Response(ResponseStatus.OK, content) {

    constructor(content: String): this(content.toByteArray())

    constructor(content: Byte): this(content.toString())

    constructor(content: Short): this(content.toString())

    constructor(content: Int): this(content.toString())

    constructor(content: Long): this(content.toString())

    constructor(content: Float): this(content.toString())

    constructor(content: Double): this(content.toString())

    constructor(content: Boolean): this(content.toString())

    constructor(content: Collection<*>): this(content.toString())
}