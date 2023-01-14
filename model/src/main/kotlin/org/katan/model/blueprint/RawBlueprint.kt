package org.katan.model.blueprint

interface RawBlueprint {

    val name: String

    val version: String

    val icon: String?

    val remote: RawBlueprintRemote

    val build: RawBlueprintBuild

    val options: List<RawBlueprintOption>
}

interface RawBlueprintOption {
    val name: String

    val type: List<String>

    val env: String?

    val defaultValue: String?
}

interface RawBlueprintRemote {

    val main: String

    val origin: String

    val exports: List<String>
}

interface RawBlueprintBuild {

    val image: String

    val entrypoint: String

    val env: Map<String, String>

    val instance: RawBlueprintInstance?
}

interface RawBlueprintInstance {

    val name: String?
}
