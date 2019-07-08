package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.GetMapping
import ru.scratty.nettyrestmapper.annotation.PostMapping
import ru.scratty.nettyrestmapper.annotation.RestController
import ru.scratty.nettyrestmapper.response.Response
import ru.scratty.nettyrestmapper.response.ResponseStatus
import java.util.concurrent.atomic.AtomicLong

@RestController("/test")
class TestController {

    private val counter = AtomicLong()

    @GetMapping("/get")
    fun getHelloWorld(): Response {
        println(counter.incrementAndGet())

        return Response(ResponseStatus.OK)
    }

    @GetMapping("/gzip")
    fun downloadGZip(): Response {
        val content = javaClass.classLoader.getResourceAsStream("eznMiWJoLTo.jpg.gz")!!.readAllBytes()
        return Response(ResponseStatus.OK, content).apply {
            contentType = "application/gzip"
        }
    }

    @PostMapping("/post")
    fun postHelloWorld(): Response = Response(ResponseStatus.OK, "{\"field\":\"value\"}").apply {
        contentType = "application/json"
    }
}