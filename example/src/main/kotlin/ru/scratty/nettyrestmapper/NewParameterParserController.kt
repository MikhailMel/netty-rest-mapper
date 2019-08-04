package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.mapping.GetMapping
import ru.scratty.nettyrestmapper.annotation.mapping.RestController
import ru.scratty.nettyrestmapper.annotation.parameter.QueryParam
import ru.scratty.nettyrestmapper.response.OkResponse

@RestController("/parser")
class NewParameterParserController {

    @GetMapping("/test1")
    fun test1(
        @QueryParam byte: Byte,
        @QueryParam short: Short,
        @QueryParam int: Int,
        @QueryParam long: Long,
        @QueryParam char: Char,
        @QueryParam float: Float,
        @QueryParam double: Double,
        @QueryParam string: String
    ) = OkResponse(
        "byte: $byte\nshort: $short\nint: $int\nlong: $long\nchar: $char\nfloat: $float\ndouble:$double\nstring: $string"
    )

    @GetMapping("/test2")
    fun test2(
        @QueryParam(required = false) int: Int?
    ) = OkResponse("int: $int")

    @GetMapping("/test3")
    fun test3(
        @QueryParam list: List<Int>
    ) = OkResponse(list.toString())
}