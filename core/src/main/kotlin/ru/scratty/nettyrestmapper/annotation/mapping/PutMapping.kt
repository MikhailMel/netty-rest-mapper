package ru.scratty.nettyrestmapper.annotation.mapping

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PutMapping(
    val path: String = ""
)