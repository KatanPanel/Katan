package me.devnatan.katan.core.crypto

import me.devnatan.katan.api.security.crypto.SaltedHash
import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import java.security.SecureRandom
import java.util.*

class BcryptHash(
    private val costFactor: Int = 12
) : SaltedHash {

    companion object {
        const val SALT_LENGTH = 16
        val random: Random = SecureRandom()
    }

    override val name: String get() = "Bcrypt"
    override val saltLength: Int get() = SALT_LENGTH

    private fun generateSalt(): ByteArray {
        return ByteArray(saltLength).apply {
            random.nextBytes(this)
        }
    }

    override fun hash(value: CharArray): String {
        return OpenBSDBCrypt.generate(value, generateSalt(), costFactor)
    }

    override fun compare(value: CharArray, hash: String): Boolean {
        return OpenBSDBCrypt.checkPassword(hash, value)
    }

}