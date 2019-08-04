package ru.scratty.nettyrestmapper.annotation.parameter

@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
annotation class PathParam(
    val name: String = ""
)