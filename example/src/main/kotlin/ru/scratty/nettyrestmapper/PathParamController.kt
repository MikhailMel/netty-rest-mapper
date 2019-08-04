package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.mapping.*
import ru.scratty.nettyrestmapper.annotation.parameter.PathParam
import ru.scratty.nettyrestmapper.response.OkResponse
import ru.scratty.nettyrestmapper.response.Response
import ru.scratty.nettyrestmapper.response.ResponseStatus

@RestController("/path-param")
class PathParamController {

    @GetMapping("/{id}")
    fun test1(@PathParam id: Int) = OkResponse(id)

    @PostMapping("/post/{city}/{category}")
    fun test2(
        @PathParam city: String,
        @PathParam("category") cat: String
    ) = OkResponse("{\"city\":\"$city\", \"category\":\"$cat\"}").apply {
        contentType = "application/json"
    }

    @DeleteMapping("/{id}")
    fun test3(@PathParam("id") deviceId: Int) = OkResponse("Deleted id: $deviceId")

    @PatchMapping("/{id}/{status}")
    fun test4(
        @PathParam id: Int,
        @PathParam status: Boolean
    ) = OkResponse("$id : $status")

    @GetMapping("/gzip/{filename}")
    fun test5(@PathParam filename: String): Response {
        val file = if (filename.subSequence(filename.length - 3, filename.length) != ".gz") {
            "$filename.gz"
        } else {
            filename
        }

        val stream = javaClass.classLoader.getResourceAsStream(file)

        return if (stream == null) {
            Response(ResponseStatus.NOT_FOUND)
        } else {
            return OkResponse(stream.readAllBytes()).apply {
                contentType = "application/gzip"
            }
        }
    }
}