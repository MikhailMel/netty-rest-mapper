package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.GetMapping
import ru.scratty.nettyrestmapper.annotation.PathParam
import ru.scratty.nettyrestmapper.annotation.PostMapping
import ru.scratty.nettyrestmapper.annotation.RestController
import ru.scratty.nettyrestmapper.response.OkResponse
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

    @GetMapping("/gzip/{filename}")
    fun downloadGZip(
        @PathParam filename: String
    ): Response {
        val file = if (filename.subSequence(filename.length - 3, filename.length) != ".gz") {
            "$filename.gz"
        } else {
            filename
        }

        val content = javaClass.classLoader.getResourceAsStream(file)!!.readAllBytes()
        return OkResponse(content).apply {
            contentType = "application/gzip"
        }
    }

    @GetMapping("/{id}")
    fun getById(
        @PathParam id: Int
    ) = OkResponse(id)

    @GetMapping("/device/{status}/")
    fun getDeviceByStatus(
        @PathParam status: Boolean
    ) = Response(ResponseStatus.OK, status.toString())

    @GetMapping("/device/{city}/{status}")
    fun getDeviceByCityAndStatus(
        @PathParam city: String,
        @PathParam status: Boolean
    ) = Response(ResponseStatus.OK, "$city $status")

    @GetMapping("/place/{category}/{city}")
    fun getPlaceByCategoryAndCity(
        @PathParam("category") cat: String,
        @PathParam("city") c: String
    ) = Response(ResponseStatus.OK, "{\"category\":\"$cat\", \"city\":\"$c\"}").apply {
        contentType = "application/json"
    }

    @PostMapping("/post")
    fun postHelloWorld(): Response = Response(ResponseStatus.OK, "{\"field\":\"value\"}").apply {
        contentType = "application/json"
    }
}