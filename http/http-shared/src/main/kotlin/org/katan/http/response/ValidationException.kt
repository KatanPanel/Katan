package org.katan.http.response

import jakarta.validation.Validator
import kotlinx.serialization.Serializable
import org.katan.http.response.HttpError.Companion.FailedToParseRequestBody

@Serializable
data class ValidationErrorResponse(
    val code: Int,
    val message: String,
    val details: Set<ValidationConstraintViolation>
)

@Serializable
data class ValidationConstraintViolation(
    val property: String,
    val info: List<String>
)

internal data class ValidationException(
    val data: ValidationErrorResponse
) : RuntimeException()

fun Validator.validateOrThrow(value: Any) {
    val violations = validate(value)
    if (violations.isEmpty()) {
        return
    }

    val mappedViolations = violations.groupBy {
        it.propertyPath
    }.map { (path, violation) ->
        ValidationConstraintViolation(
            property = path.toString(),
            info = violation.map { it.message }
        )
    }.toSet()

    throw ValidationException(
        ValidationErrorResponse(
            code = FailedToParseRequestBody.code,
            message = FailedToParseRequestBody.message,
            details = mappedViolations
        )
    )
}
