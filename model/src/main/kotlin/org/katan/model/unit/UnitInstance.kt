package org.katan.model.unit

interface UnitInstance {

    val id: Long

    val image: String

    val runtimeId: String

}

private data class UnitInstanceImpl(
    override val id: Long,
    override val image: String,
    override val runtimeId: String
) : UnitInstance

fun UnitInstance(id: Long, dockerImage: String, container: String): UnitInstance {
    return UnitInstanceImpl(id, dockerImage, container)
}