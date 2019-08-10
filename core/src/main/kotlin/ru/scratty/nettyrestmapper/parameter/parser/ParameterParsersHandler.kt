package ru.scratty.nettyrestmapper.parameter.parser

import ru.scratty.nettyrestmapper.annotation.parameter.parser.ParameterParser
import ru.scratty.nettyrestmapper.annotation.parameter.parser.ParameterParsersHolder
import ru.scratty.nettyrestmapper.exception.AddParameterParserException
import ru.scratty.nettyrestmapper.parameter.FunctionParameter

class ParameterParsersHandler(
    private val parsers: List<Any>
) {

    private val mutableMapWithParsers: MutableMap<Class<*>, ParameterParserFunction> = createParameterParserFunctionsMap()

    val parameterParsers = mutableMapWithParsers as Map<Class<*>, ParameterParserFunction>

    fun addParsers(vararg parsersClasses: Any) {
        parsersClasses.forEach {
            parseClass(it, mutableMapWithParsers)
        }
    }

    private fun createParameterParserFunctionsMap(): MutableMap<Class<*>, ParameterParserFunction> {
        val map = mutableMapOf<Class<*>, ParameterParserFunction>()

        parsers.forEach {
            parseClass(it, map)
        }

        return map
    }

    private fun parseClass(parsersClass: Any, map: MutableMap<Class<*>, ParameterParserFunction>) {
        val annotationClass = parsersClass::class.java.getAnnotation(ParameterParsersHolder::class.java)
        val overwriteIfExistsInClass = annotationClass?.overwriteIfExists ?: false

        val methods = parsersClass::class.java.methods

        methods.forEach { method ->
            method.getAnnotation(ParameterParser::class.java)?.let {
                method.parameterTypes.apply {
                    if (size != 1 && !contains(String::class.java)
                        && (size != 2 || !contains(String::class.java) || !contains(FunctionParameter::class.java))
                    ) {
                        throw AddParameterParserException("Parameter parser method '${method.name}' must have one parameter String or two parameters: String and FunctionParameter")
                    }
                }

                if (overwriteIfExistsInClass || it.overwriteIfExists || !map.containsKey(method.returnType)) {
                    map[method.returnType] = ParameterParserFunction(method, parsersClass)
                } else {
                    throw AddParameterParserException(
                        "Parameter parser for type '${method.returnType.typeName}' is exists, set overwriteIfExists=true" +
                                " in ParameterParser annotation for overwriting parser, method '${method.name}'"
                    )
                }
            }
        }
    }
}