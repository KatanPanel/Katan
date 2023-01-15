package org.katan.model.blueprint

interface BlueprintSpec {

    val name: String

    val version: String

    val remote: BlueprintSpecRemote

    val build: BlueprintSpecBuild

    val options: List<BlueprintSpecOption>
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

    val image: BlueprintSpecBuildImage

    val entrypoint: String

    val env: Map<String, String>

    val instance: BlueprintSpecBuildInstance?
}

interface BlueprintSpecBuildInstance {

    val name: String?
}

interface BlueprintSpecBuildImage {

    interface Single : BlueprintSpecBuildImage {
        val id: String
    }

    interface Ref : BlueprintSpecBuildImage {
        val ref: String

        val tag: String
    }

    interface Multiple : BlueprintSpecBuildImage {
        val images: List<Single>
    }
}
