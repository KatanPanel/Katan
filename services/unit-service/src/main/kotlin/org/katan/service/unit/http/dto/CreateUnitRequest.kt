package org.katan.service.unit.http.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Docker Image Specification v1.2.0
// https://github.com/moby/moby/blob/master/image/spec/v1.2.md
private const val IMAGE_LENGTH = 128
private const val IMAGE_REGEX = ".*[a-zA-Z0-9_.-]"

@Serializable
internal data class CreateUnitRequest(
    @field:NotBlank(message = "Name must be provided.")
    @field:Size(
        min = 2,
        max = 64,
        message = "Name must have a minimum length of {min} and at least {max} characters."
    )
    val name: String? = null,

    @SerialName("image")
    @field:NotBlank(message = "Image must be provided.")
    @field:Pattern(regexp = IMAGE_REGEX, message = "Image does not follow image name pattern.")
    @field:Size(max = IMAGE_LENGTH, message = "Image cannot exceed {max} characters.")
    val image: String? = null,

    val network: Network?
) {

    @Serializable
    internal data class Network(
        val host: String?,
        val port: Int?
    )
}
