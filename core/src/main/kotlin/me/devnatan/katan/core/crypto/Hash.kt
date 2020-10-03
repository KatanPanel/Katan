package me.devnatan.katan.core.crypto

interface Hash {

    fun hash(bytes: String): ByteArray

    fun compare(value: String, hash: ByteArray): Boolean

}