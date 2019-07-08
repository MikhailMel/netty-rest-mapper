package ru.scratty.nettyrestmapper.exception

class FewMethodsHandleException(httpMethod: String, path: String): Exception("Few methods handle $httpMethod '$path'")