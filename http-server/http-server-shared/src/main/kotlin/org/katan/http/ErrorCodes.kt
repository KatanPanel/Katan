package org.katan.http

@kotlinx.serialization.Serializable
data class HttpError(
    val code: Int,
    val message: String
) {

    companion object {

        val ServerNotFound = HttpError(1001, "Server not found")
        val ServerConflict = HttpError(1002, "Server already exists")
        val ServerMissingCreateOptions = HttpError(1003, "Missing create options")

    }

}