package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
annotation class PathParam(
    val name: String = ""
)