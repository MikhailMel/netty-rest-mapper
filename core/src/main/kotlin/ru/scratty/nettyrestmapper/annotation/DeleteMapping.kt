package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class DeleteMapping(
    val path: String = ""
)