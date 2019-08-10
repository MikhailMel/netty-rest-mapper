package ru.scratty.nettyrestmapper.annotation.parameter.parser

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class ParameterParsersHolder(
    val overwriteIfExists: Boolean = false
)