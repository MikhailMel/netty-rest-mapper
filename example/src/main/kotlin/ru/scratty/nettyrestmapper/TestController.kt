package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.GetMapping
import ru.scratty.nettyrestmapper.annotation.PostMapping
import ru.scratty.nettyrestmapper.annotation.RestController

@RestController("/test")
class TestController {

    @GetMapping("/get")
    fun getHelloWorld() {
        println("GET")
    }

    @PostMapping("/post")
    fun postHelloWorld() {
        println("POST")
    }
}