package ru.scratty.nettyrestmapper

data class FunctionParameter(
    val name: String = "",
    val variableType: Class<*> = Any::class.java,
    val parameterType: ParamType = ParamType.UNDEFINED,
    val required: Boolean = true,
    val default: String = ""
) {

    enum class ParamType {
        UNDEFINED,
        PATH_PARAM,
        QUERY_PARAM,
        REQUEST_BODY
    }
}