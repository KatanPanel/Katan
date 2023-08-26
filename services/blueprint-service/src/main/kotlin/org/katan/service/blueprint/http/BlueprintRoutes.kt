package org.katan.service.blueprint.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake
import org.katan.service.id.validation.MustBeSnowflake

@Serializable
@Resource("/blueprints")
internal class BlueprintRoutes {

    @Serializable
    @Resource("")
    internal class All(val parent: BlueprintRoutes = BlueprintRoutes())

    @Serializable
    @Resource("{blueprintId}")
    internal class ById(
        val parent: BlueprintRoutes = BlueprintRoutes(),
        @field:MustBeSnowflake val blueprintId: String
    )

    @Serializable
    @Resource("import")
    internal class Import(val parent: BlueprintRoutes = BlueprintRoutes())
}
