package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PatchMapping(
    val path: String = ""
)