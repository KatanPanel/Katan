package org.katan.service.blueprint.repository

interface BlueprintEntity {

    var name: String

    var image: String

    fun getId(): Long

}