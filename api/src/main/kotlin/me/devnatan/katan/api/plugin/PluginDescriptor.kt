package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Version
import java.util.*

/**
 * It represents the basic information of a plugin, as it should be described,
 * it is also used as a dependency filtering element for example through the version of the descriptor.
 */
class PluginDescriptor(
    val name: String,
    val version: Version? = null,
    val author: String? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PluginDescriptor

        if (name != other.name) return false
        if (version != other.version) return false
        if (author != other.author) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(name, version, author)
    }

    override fun toString(): String {
        return buildString {
            append(name)
            version?.run { append(" v$this") }
            author?.run { append(" by $this") }
        }
    }

}