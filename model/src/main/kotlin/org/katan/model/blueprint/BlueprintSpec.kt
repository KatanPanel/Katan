package org.katan.model.blueprint

interface BlueprintSpec {

    val name: String

    val version: String

    val remote: BlueprintSpecRemote?

    val build: BlueprintSpecBuild?

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

    val image: BlueprintSpecImage

    val entrypoint: String

    val env: Map<String, String>

    val instance: BlueprintSpecInstance?
}

interface BlueprintSpecInstance {

    val name: String?
}

interface BlueprintSpecImage {
    interface Identifier : BlueprintSpecImage {
        val id: String
    }

    interface Ref : BlueprintSpecImage {
        val ref: String
        val tag: String
    }

    interface Multiple : BlueprintSpecImage {
        val images: List<Ref>
    }
}
