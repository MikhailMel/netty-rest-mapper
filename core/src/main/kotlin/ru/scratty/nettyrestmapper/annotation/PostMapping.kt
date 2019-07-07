package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PostMapping(
    val path: String = ""
)