package org.katan.service.blueprint

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import org.katan.http.WithHttpError
import org.katan.http.response.HttpError.Companion.RawBlueprintNotFound
import org.katan.http.response.HttpError.Companion.RawBlueprintParse
import org.katan.http.response.HttpError.Companion.UnknownBlueprint
import org.katan.model.KatanException

open class BlueprintException : KatanException()

class BlueprintNotFoundException : BlueprintException(), WithHttpError {

    override val httpError get() = UnknownBlueprint
    override val status get() = BadRequest

}

class NoMatchingProviderException : BlueprintException()

class RemoteRawBlueprintNotFound : BlueprintException(), WithHttpError {

    override val httpError get() = RawBlueprintNotFound
    override val status get() = BadRequest

}

class BlueprintConflictException : BlueprintException()

class RawBlueprintParseException(override val message: String) : BlueprintException(),
    WithHttpError {

    override val httpError get() = RawBlueprintParse(message)
    override val status get() = UnprocessableEntity

}