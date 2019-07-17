package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.GetMapping
import ru.scratty.nettyrestmapper.annotation.QueryParam
import ru.scratty.nettyrestmapper.annotation.RestController
import ru.scratty.nettyrestmapper.response.OkResponse

@RestController("/query-param")
class QueryParamController {

    @GetMapping("/test1")
    fun test1(@QueryParam number: Int) = OkResponse(number)

    @GetMapping("/test2")
    fun test2(
        @QueryParam(required = false, default = "13") number: Int,
        @QueryParam("str") s: String
    ) = OkResponse("number: $number str: $s")

    @GetMapping("/test3")
    fun test3(
        @QueryParam(required = false, default = "false") status: Boolean,
        @QueryParam(required = false, default = "a") sim: Char
    ) = OkResponse("status: $status sim: $sim")

    @GetMapping("/test4")
    fun test4(
        @QueryParam(required = false) double: Double?,
        @QueryParam(required = false) string: String?,
        @QueryParam(required = false) long: Long?
    ) = OkResponse("double: $double string: $string long: $long")
}