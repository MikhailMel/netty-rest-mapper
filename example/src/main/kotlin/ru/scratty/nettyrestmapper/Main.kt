package ru.scratty.nettyrestmapper

class Main

fun main() {
    val server = Server(
        8080,
        ControllerHandler(listOf(TestController()))
    )
    server.startServer()
}