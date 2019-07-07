package ru.scratty.nettyrestmapper

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

class HttpMethodHandler(
    val httpMethod: HttpMethod,
    private val path: String,
    private val method: Method,
    private val handler: Any
) {

    private val pathPattern: Regex = (path + URL_QUERY_REGEX).toRegex()

    companion object {
        private const val URL_QUERY_REGEX = "(?:\\?.*?)?$"

        private val log = LoggerFactory.getLogger(HttpMethodHandler::class.java)
    }

    fun isPathMatched(path: String): Boolean = pathPattern.matches(path)

    fun invoke(args: Array<*>) {
        try {
            if (args.isEmpty()) {
                method.invoke(handler)
            } else {
                method.invoke(handler, args)
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    override fun toString(): String {
        return "$httpMethod $path"
    }


}