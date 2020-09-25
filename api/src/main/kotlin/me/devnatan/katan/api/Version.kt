package me.devnatan.katan.api

class Version(
    val major: Int,
    val minor: Int,
    val patch: Int
) {

    private val version = major.shl(16) + minor.shl(8) + patch

    override fun toString(): String = "$major.$minor.$patch"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherVersion = (other as? Version) ?: return false
        return this.version == otherVersion.version
    }

    override fun hashCode(): Int = version

}