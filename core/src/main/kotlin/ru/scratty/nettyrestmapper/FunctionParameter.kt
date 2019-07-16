package ru.scratty.nettyrestmapper

data class FunctionParameter(
    val name: String,
    val variableType: Class<*>,
    val parameterType: ParamType,
    val required: Boolean = true,
    val default: String = ""
) {

    enum class ParamType {
        UNDEFINED,
        PATH_PARAM,
        QUERY_PARAM
    }
}