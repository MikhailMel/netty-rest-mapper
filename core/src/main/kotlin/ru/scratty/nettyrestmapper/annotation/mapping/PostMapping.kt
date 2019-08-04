package ru.scratty.nettyrestmapper.annotation.mapping

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PostMapping(
    val path: String = ""
)