package ru.scratty.nettyrestmapper.annotation.mapping

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PatchMapping(
    val path: String = ""
)