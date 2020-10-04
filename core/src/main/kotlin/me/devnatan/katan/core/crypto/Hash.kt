package me.devnatan.katan.core.crypto

interface Hash {

    fun hash(value: CharArray): String

    fun compare(value: CharArray, hash: String): Boolean

}