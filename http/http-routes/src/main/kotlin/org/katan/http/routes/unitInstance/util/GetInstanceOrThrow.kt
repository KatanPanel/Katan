package org.katan.http.routes.unitInstance

import io.ktor.server.application.ApplicationCall
import org.katan.http.InvalidUnitIdFormat
import org.katan.http.UnitInstanceNotFound
import org.katan.http.respondError
import org.katan.model.unit.UnitInstance

private typealias PipelineContext = io.ktor.util.pipeline.PipelineContext<Unit, ApplicationCall>

internal suspend fun PipelineContext.getInstanceOrThrow(
    block: suspend () -> UnitInstance?
): UnitInstance {
    return runCatching {
        block()
    }.onFailure { error ->
        when (error) {
            is IllegalArgumentException -> respondError(InvalidUnitIdFormat)
            else -> throw error
        }
    }.getOrNull() ?: respondError(UnitInstanceNotFound)
}
