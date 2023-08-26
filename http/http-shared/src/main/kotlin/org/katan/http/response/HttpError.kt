package org.katan.http.response

import kotlinx.serialization.Serializable

@Serializable
data class HttpError internal constructor(
    val code: Int,
    val message: String,
    val details: String?
) {

    companion object {

        val Generic: (String) -> HttpError = { message -> createError(0, message) }
        val UnknownAccount = createError(1001, "Unknown account")
        val UnknownUnit = createError(1002, "Unknown unit")
        val UnknownInstance = createError(1003, "Unknown instance")
        val UnknownFSBucket = createError(1004, "Unknown file system bucket")
        val UnknownFSFile = createError(1005, "Unknown file")
        val InstanceRuntimeNotAvailable = createError(1006, "Instance runtime not available")
        val ResourceNotAccessible = createError(1007, "Resource not accessible")
        val FileIsNotDirectory = createError(1008, "File is not a directory")
        val RequestedResourceIsNotAFile = createError(1009, "The requested resource is not a file")
        val UnavailableFileSystem = createError(1010, "Unavailable file system")
        val UnknownBlueprint = createError(1011, "Unknown blueprint")
        val BlueprintParse: (String) -> HttpError =
            { createError(1012, "Failed to parse blueprint file", it) }
        val InvalidAccessToken = createError(2001, "Invalid or missing access token")
        val AccountInvalidCredentials = createError(2002, "Invalid account credentials")
        val AccountLoginConflict = createError(
            2003,
            "An account with that username or email already exists"
        )
        val InvalidInstanceUpdateCode = createError(3001, "Invalid instance update code")
        val FailedToParseRequestBody = createError(3002, "Failed to handle request")
        val InvalidRequestBody = createError(3003, "Invalid request body")

        @JvmStatic
        fun createError(code: Int, message: String, details: String? = null): HttpError {
            return HttpError(code, message, details)
        }
    }
}
