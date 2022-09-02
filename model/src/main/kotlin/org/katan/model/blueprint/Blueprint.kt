package org.katan.model.blueprint

interface Blueprint {

    val id: Long

    val name: String

    val image: String

    val env: Map<String, String>

    val remote: BlueprintRemote

    val isVerified: Boolean

    val isOfficial: Boolean
}
