package me.devnatan.katan.common.util

import java.util.regex.Pattern

open class StringReplacer(value: String) {

    protected var _value: String = value

    open infix fun String.by(other: Any) {
        _value = _value.replace(this, other.toString())
    }

    override fun toString(): String {
        return _value
    }

}

class EnvironmentVarStringReplacer(
    value: String,
    private val replacements: MutableMap<String, Any> = HashMap()
) : StringReplacer(value) {

    companion object {
        private val PATTERN: Pattern = Pattern.compile("\\$\\{([A-Za-z0-9_.-]+)(?::([^}]*))?}");
    }

    override infix fun String.by(other: Any) {
        replacements[this] = other.toString()
    }

    override fun toString(): String {
        val matcher = PATTERN.matcher(_value)
        var index = 0
        return buildString {
            while (matcher.find()) {
                append(_value, index, matcher.start())
                append(
                    replacements[matcher.group(1)]?.toString()
                        ?: (matcher.group(2) ?: "")
                )
                index = matcher.end()
            }

            append(_value, index, _value.length)
        }
    }

}

class PrefixStringReplacer(
    value: String,
    private val prefix: String,
    private val suffix: String,
    private val replacements: MutableMap<String, Any> = HashMap()
) : StringReplacer(value) {

    override infix fun String.by(other: Any) {
        replacements[this] = other.toString()
    }

    override fun toString(): String {
        for (replacement in replacements) {
            _value = _value.replace(prefix + replacement.key + suffix, replacement.value.toString())
        }

        return _value
    }

}

inline fun String.replaceEach(block: StringReplacer.() -> Unit): String {
    return StringReplacer(this).apply(block).toString()
}

inline fun String.replaceEnvironmentVars(block: StringReplacer.() -> Unit): String {
    return EnvironmentVarStringReplacer(this).apply(block).toString()
}

fun String.replaceEnvironmentVars(replacements: Map<String, Any>): String {
    return EnvironmentVarStringReplacer(this, replacements.toMutableMap()).toString()
}

inline fun String.replaceBetween(between: String = "", block: StringReplacer.() -> Unit): String {
    return PrefixStringReplacer(this, between, between).apply(block).toString()
}

inline fun String.replaceBetween(prefix: String = "", suffix: String = "", block: StringReplacer.() -> Unit): String {
    return PrefixStringReplacer(this, prefix, suffix).apply(block).toString()
}