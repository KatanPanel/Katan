package me.devnatan.katan.api

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String = "",
    val buildMetadata: String = ""
) {

    companion object {

        // https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
        internal val REGEX =
            "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$"
                .toRegex()

    }

    override fun toString(): String {
        return buildString {
            append("$major.$minor.$patch")
            if (preRelease.isNotEmpty())
                append("-$preRelease")

            if (buildMetadata.isNotEmpty())
                append(" (build $buildMetadata)")
        }
    }

}

fun Version(version: CharSequence): Version {
    if (!Version.REGEX.matches(version))
        throw IllegalArgumentException("\"$version\" does not match the versioning rules.")

    val (major, minor, patch, preRelease, buildMetadata) = Version.REGEX.find(version)!!.destructured
    return Version(major.toInt(), minor.toInt(), patch.toInt(), preRelease, buildMetadata)
}