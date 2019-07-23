package ru.scratty.nettyrestmapper.exception

class ParameterMissingException(parameterName: String): Exception("Required parameter '$parameterName' is missing")