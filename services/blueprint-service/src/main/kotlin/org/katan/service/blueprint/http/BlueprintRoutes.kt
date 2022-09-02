package org.katan.service.blueprint.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.katan.service.id.validation.MustBeSnowflake

@Serializable
@Resource("/blueprints")
internal class BlueprintRoutes {

    @Serializable
    @Resource("{blueprintId}")
    internal class ById(
        @Suppress("UNUSED") val parent: BlueprintRoutes = BlueprintRoutes(),
        @field:MustBeSnowflake val blueprintId: String
    )

}
