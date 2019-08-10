package ru.scratty.nettyrestmapper.annotation.parameter.parser

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class ParameterParser(
    val overwriteIfExists: Boolean = false
)