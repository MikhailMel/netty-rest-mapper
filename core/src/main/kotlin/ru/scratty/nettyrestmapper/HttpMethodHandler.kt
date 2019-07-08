package ru.scratty.nettyrestmapper

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.response.Response
import ru.scratty.nettyrestmapper.response.ResponseStatus
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

    fun invoke(args: Array<*>): Response {
        try {
            val result = if (args.isEmpty()) {
                method.invoke(handler)
            } else {
                method.invoke(handler, args)
            }

            return if (method.returnType == Response::class.java) {
                result as Response
            } else {
                Response(ResponseStatus.OK)
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }

        return Response(ResponseStatus.INTERNAL_SERVER_ERROR)
    }

    override fun toString(): String {
        return "$httpMethod $path"
    }


}