package org.katan.service.blueprint.http.dto

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable
import org.hibernate.validator.constraints.URL

@Serializable
internal data class ImportBlueprintRequest(
    @field:NotBlank(message = "Url must be provided")
    @field:URL(message = "Input is not a valid url")
    val url: String = ""
)
