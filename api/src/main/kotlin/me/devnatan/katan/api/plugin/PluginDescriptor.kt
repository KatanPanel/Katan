package me.devnatan.katan.api.plugin

import me.devnatan.katan.api.Version
import java.util.*

/**
 * It represents the basic information of a plugin, as it should be described,
 * it is also used as a dependency filtering element for example through the version of the descriptor.
 *
 * All values of this class must be filled in, they are defined as lateinit (not "var" with default value)
 * because they must be initialized during the start process of a plugin and why if there
 * is no need to use any of the fields if it is not requested it does not need to be initialized.
 *
 */
class PluginDescriptor {

    lateinit var name: String
    lateinit var version: Version
    lateinit var author: String

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
        return "$name v$version by $author"
    }

}