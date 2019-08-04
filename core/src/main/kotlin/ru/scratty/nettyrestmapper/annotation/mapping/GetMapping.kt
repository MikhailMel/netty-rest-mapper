package ru.scratty.nettyrestmapper.annotation.mapping

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class GetMapping(
    val path: String = ""
)