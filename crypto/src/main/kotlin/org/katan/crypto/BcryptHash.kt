package org.katan.crypto

import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import java.security.SecureRandom
import java.util.Random

/**
 * Bcrypt salted hash implementation.
 */
internal class BcryptHash : SaltedHash {

    companion object {
        private val rand: Random = SecureRandom()
    }

    override val name: String = "Bcrypt"
    override val saltLength: Int = 16

    private fun generateSalt(): ByteArray {
        return ByteArray(saltLength).also {
            rand.nextBytes(it)
        }
    }

    override fun hash(value: CharArray): String {
        return OpenBSDBCrypt.generate(value, generateSalt(), 12)
    }

    override fun compare(value: CharArray, hash: String): Boolean {
        return OpenBSDBCrypt.checkPassword(hash, value)
    }
}
