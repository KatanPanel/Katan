package org.katan.service.blueprint.http.routes

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.charset
import io.ktor.http.fromFileExtension
import io.ktor.http.withCharset
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.respond
import org.katan.http.response.validateOrThrow
import org.katan.model.io.FileNotFoundException
import org.katan.model.io.extension
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.http.BlueprintRoutes
import org.katan.service.blueprint.http.dto.ReadBlueprintFileResponse
import org.katan.service.fs.http.dto.FSSingleFileResponse
import org.koin.ktor.ext.inject

internal fun Route.readBlueprintFile() {
    val blueprintService by inject<BlueprintService>()
    val validator by inject<Validator>()

    get<BlueprintRoutes.ReadAsset> { params ->
        validator.validateOrThrow(params)

        val (file, contents) = try {
            blueprintService.readBlueprintAssetContents(params.blueprintId.toLong(), params.path!!)
        } catch (e: FileNotFoundException) {
            call.response.status(HttpStatusCode.NotFound)
            return@get
        }

        respond(
            ReadBlueprintFileResponse(
                file = FSSingleFileResponse(file),
                type = ContentType.fromFileExtension(file.extension).selectDefault().toString(),
                data = contents
            )
        )
    }
}

// See io.ktor.http.List<ContentType>#selectDefault()
private fun List<ContentType>.selectDefault(): ContentType {
    val contentType = firstOrNull() ?: ContentType.Application.OctetStream
    return when {
        contentType.contentType == "text" && contentType.charset() == null -> contentType.withCharset(
            Charsets.UTF_8
        )

        else -> contentType
    }
}
