package ru.scratty.nettyrestmapper

import ru.scratty.nettyrestmapper.annotation.*
import ru.scratty.nettyrestmapper.response.OkResponse

@RestController("/simple")
class SimpleMappingController {

    @GetMapping
    fun get() = OkResponse("It's get method")

    @PostMapping
    fun post() = OkResponse("It's post method")

    @DeleteMapping
    fun delete() = OkResponse("It's delete method")

    @PutMapping
    fun put() = OkResponse("It's put method")

    @PatchMapping
    fun patch() = OkResponse("It's patch method")
}