package ru.scratty.nettyrestmapper.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
annotation class RequestBody(
    val required: Boolean = true,
    val default: String = ""
)