package ru.scratty.nettyrestmapper.parameter

import kotlin.reflect.KParameter
import kotlin.reflect.jvm.jvmErasure

data class FunctionParameter(
    val kParameter: KParameter,
    val name: String = "",
    val parameterType: ParamType = ParamType.UNDEFINED,
    val required: Boolean = true,
    val default: String = ""
) {

    val variableType: Class<*> = kParameter.type.jvmErasure.java

    enum class ParamType {
        UNDEFINED,
        PATH_PARAM,
        QUERY_PARAM,
        REQUEST_BODY
    }
}