package ru.scratty.nettyrestmapper

import io.netty.handler.codec.http.HttpMethod
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.response.Response
import ru.scratty.nettyrestmapper.response.ResponseStatus
import java.lang.reflect.Method

class HttpMethodHandler(
    val httpMethod: HttpMethod,
    private val path: String,
    val pathParams: List<String> = emptyList(),
    val parameters: List<MethodParameter> = emptyList(),
    private val method: Method,
    private val handler: Any
) {

    val pathPattern: Regex = (path.removeSuffix("/")
        .replaceFirst(URL_FORMAT_REGEX.toRegex(), URL_FORMAT_MATCH_REGEX)
        .replace(URL_PARAM_REGEX.toRegex(), URL_PARAM_MATCH_REGEX) + URL_QUERY_STRING_REGEX).toRegex()

    companion object {
        private const val URL_FORMAT_REGEX = "(?:\\.\\{format})$"
        private const val URL_FORMAT_MATCH_REGEX = "(?:\\\\.\\([\\\\w%]+?\\))?"
        private const val URL_QUERY_STRING_REGEX = "/?(?:\\?.*?)?$"
        private const val URL_PARAM_REGEX = "\\{(\\w*?)}"
        private const val URL_PARAM_MATCH_REGEX = "\\([%\\\\w-.\\\\~!\\$&'\\\\(\\\\)\\\\*\\\\+,;=:\\\\[\\\\]@]+?\\)"

        private val log = LoggerFactory.getLogger(HttpMethodHandler::class.java)
    }

    fun isPathMatched(path: String): Boolean = pathPattern.matches(path)

    fun invoke(args: Array<Any?>?): Response {
        try {
            val result = if (args == null || args.isEmpty()) {
                method.invoke(handler)
            } else {
                method.invoke(handler, *args)
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
        return "$httpMethod $path $pathPattern"
    }
}