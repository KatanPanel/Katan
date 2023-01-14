package org.katan.model.blueprint

interface BlueprintSpec {

    val name: String

    val version: String

    val icon: String?

    val remote: BlueprintSpecRemote

    val build: BlueprintSpecBuild

    val options: List<BlueprintSpecOption>
}

interface BlueprintSpecOption {
    val name: String

    val type: List<String>

    val env: String?

    val defaultValue: String?
}

interface BlueprintSpecRemote {

    val main: String

    val origin: String

    val exports: List<String>
}

interface BlueprintSpecBuild {

    val image: String

    val entrypoint: String

    val env: Map<String, String>

    val instance: BlueprintSpecBuildInstance?
}

interface BlueprintSpecBuildInstance {

    val name: String?
}
