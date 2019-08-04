package ru.scratty.nettyrestmapper.annotation.mapping

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class RestController(
    val path: String = ""
)