package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.mapping.PostMapping
import ru.scratty.nettyrestmapper.annotation.parameter.RequestBody
import ru.scratty.nettyrestmapper.annotation.mapping.RestController
import ru.scratty.nettyrestmapper.response.OkResponse

@RestController("/request-body")
class RequestBodyController {

    @PostMapping("/test1")
    fun test1(
        @RequestBody str: String
    ) = OkResponse(str)

    @PostMapping("/test2")
    fun test2(
        @RequestBody number: Int
    ) = OkResponse(number)

    @PostMapping("/test3")
    fun test3(
        @RequestBody(required = false) str: String?
    ) = OkResponse("$str")

    @PostMapping("/test4")
    fun test4(
        @RequestBody(required = false, default = "1234567890") long: Long
    ) = OkResponse(long)
}