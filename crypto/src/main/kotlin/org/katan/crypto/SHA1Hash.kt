package org.katan.crypto

import org.bouncycastle.jcajce.provider.digest.SHA1

internal class SHA1Hash : Hash {

    override val name: String = "SHA1"

    private val digest = SHA1.Digest()

    override fun hash(value: CharArray): String {
        return digest.digest(value.map { it.code.toByte() }.toByteArray()).decodeToString()
    }

    override fun compare(value: CharArray, hash: String): Boolean {
        error("$name hash comparison is not supported")
    }
}
