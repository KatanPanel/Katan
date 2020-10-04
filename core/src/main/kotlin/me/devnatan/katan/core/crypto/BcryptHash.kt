package me.devnatan.katan.core.crypto

import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import java.security.SecureRandom
import java.util.*

class BcryptHash : Hash {

    companion object {
        val random: Random = SecureRandom()
    }

    private fun generateSalt() = ByteArray(16).apply {
        random.nextBytes(this)
    }

    override fun hash(value: CharArray): String {
        return OpenBSDBCrypt.generate(value, generateSalt(), 12)
    }

    override fun compare(value: CharArray, hash: String): Boolean {
        return OpenBSDBCrypt.checkPassword(hash, value)
    }

}