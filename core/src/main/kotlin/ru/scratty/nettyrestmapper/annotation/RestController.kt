package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class RestController(
    val path: String = ""
)