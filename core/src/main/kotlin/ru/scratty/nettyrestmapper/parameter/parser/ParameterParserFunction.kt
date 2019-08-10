package ru.scratty.nettyrestmapper.parameter.parser

import ru.scratty.nettyrestmapper.parameter.FunctionParameter
import java.lang.reflect.Method

class ParameterParserFunction(
    private val method: Method,
    private val handler: Any
) {

    fun invoke(parameter: FunctionParameter, value: String): Any? {
        val parameterTypes = method.parameterTypes

        return if (parameterTypes.size == 1) {
            method.invoke(handler, value)
        } else if (parameterTypes[0] == FunctionParameter::class.java && parameterTypes[1] == String::class.java) {
            method.invoke(handler, parameter, value)
        } else {
            method.invoke(handler, value, parameter)
        }
    }
}