package org.katan.model.blueprint

interface BlueprintSpec {

    val name: String

    val version: String

    val remote: BlueprintSpecRemote

    val build: BlueprintSpecBuild

    val options: BlueprintSpecOptions
}

typealias BlueprintSpecOptions = List<BlueprintSpecOption>

fun BlueprintSpecOptions(): BlueprintSpecOptions {
    return emptyList()
}

interface BlueprintSpecOption {

    val id: String

    val name: String

    val type: List<String>

    val env: String?

    val defaultValue: String?
}

interface BlueprintSpecRemote {

    val origin: String
}

interface BlueprintSpecBuild {

    val image: String?

    val images: List<BlueprintSpecBuildImage>?

    val entrypoint: String

    val env: Map<String, String>

    val instance: BlueprintSpecBuildInstance?
}

interface BlueprintSpecBuildInstance {

    val name: String?
}

interface BlueprintSpecBuildImage {
    val ref: String

    val tag: String
}
