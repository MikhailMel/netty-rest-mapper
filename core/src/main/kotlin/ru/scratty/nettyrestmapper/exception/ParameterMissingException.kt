package ru.scratty.nettyrestmapper.exception

import ru.scratty.nettyrestmapper.FunctionParameter

class ParameterMissingException(parameter: FunctionParameter) : Exception(
    "Required ${if (parameter.parameterType == FunctionParameter.ParamType.REQUEST_BODY) {
        "request body"
    } else {
        "parameter '${parameter.name}'"
    }} is missing"
)