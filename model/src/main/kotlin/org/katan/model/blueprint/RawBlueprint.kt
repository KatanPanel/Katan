package org.katan.model.blueprint

import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.io.path.relativeToOrSelf

interface RawBlueprint {

    val name: String

    val version: String

    val icon: String?

    val type: String

    val remote: RawBlueprintRemote

    val build: RawBlueprintBuild

    val instance: RawBlueprintInstanceSettings?

}

interface RawBlueprintRemote {

    val main: String

    val origin: String?

    val provider: String?

}

interface RawBlueprintBuild {

    val image: String

    val entrypoint: String

    val env: Map<String, String>?

}

interface RawBlueprintInstanceSettings {

    val name: String?

}

fun RawBlueprint.normalizedIcon(): String? {
    if (remote.origin == null)
        return icon

    return icon?.let {
        val path = Paths.get(it)
        if (path.isAbsolute)
            return it

        var relative = it
        if (relative.first() == '.')
            relative = relative.substring(1)

        return remote.origin!! + relative
    }
}