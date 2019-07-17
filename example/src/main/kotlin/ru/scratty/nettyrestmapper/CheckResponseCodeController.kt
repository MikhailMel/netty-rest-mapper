package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.GetMapping
import ru.scratty.nettyrestmapper.annotation.PostMapping
import ru.scratty.nettyrestmapper.annotation.RestController
import ru.scratty.nettyrestmapper.response.OkResponse
import ru.scratty.nettyrestmapper.response.Response
import ru.scratty.nettyrestmapper.response.ResponseStatus

@RestController("/response")
class CheckResponseCodeController {

    @GetMapping("/test1")
    fun test1() = Response(ResponseStatus.OK)

    @GetMapping("/test2")
    fun test2() = Response(ResponseStatus.OK, "WOW it's content")

    @GetMapping("/test3")
    fun test3() = OkResponse()

    @GetMapping("/test4")
    fun test4() = OkResponse(listOf(1, 2, 3, 4, 5))

    @PostMapping("/test5")
    fun test5() = Response(ResponseStatus.BAD_GATEWAY, "ByteArray:)".toByteArray())

    @PostMapping("/test6")
    fun test6() = Response(ResponseStatus.INTERNAL_SERVER_ERROR, "abc".toCharArray())

    @PostMapping("/test7")
    fun test7() = Response(ResponseStatus.NOT_FOUND, "Sad:(")
}