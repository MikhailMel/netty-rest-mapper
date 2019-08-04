package ru.scratty.nettyrestmapper.annotation.mapping

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class DeleteMapping(
    val path: String = ""
)