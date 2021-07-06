/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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