package me.devnatan.katan.common

class StringReplacer(value: String) {

    private var _value = value

    infix fun String.by(other: String) {
        _value = _value.replace(this, other)
    }

    override fun toString(): String {
        return _value
    }

}

inline fun String.replaceEach(block: StringReplacer.() -> Unit): String {
    return StringReplacer(this).apply(block).toString()
}