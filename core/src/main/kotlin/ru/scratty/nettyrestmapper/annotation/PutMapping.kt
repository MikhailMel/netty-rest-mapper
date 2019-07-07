package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PutMapping(
    val path: String = ""
)