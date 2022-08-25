package org.katan.model.blueprint

interface Blueprint {

    val name: String

    val image: String

    val env: Map<String, String>
}
