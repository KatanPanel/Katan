package org.katan.service.instance.http

import io.ktor.resources.Resource
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.service.id.validation.MustBeSnowflake

@Serializable
@Resource("/instances")
internal class InstanceRoutes {

    @Serializable
    @Resource("{instanceId}")
    internal class ById(
        @Suppress("UNUSED") val parent: InstanceRoutes = InstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/status")
    internal class UpdateStatus(
        @Suppress("UNUSED") val parent: InstanceRoutes = InstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String
    )

    @Serializable
    @Resource("{instanceId}/fs/{bucket}")
    internal class FSBucket(
        @Suppress("UNUSED") val parent: InstanceRoutes = InstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String,
        @field:NotBlank(message = "Bucket must be provided") val bucket: String
    )

    @Serializable
    @Resource("{instanceId}/fs/{bucket}/file")
    internal class FSFile(
        @Suppress("UNUSED") val parent: InstanceRoutes = InstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String,
        @field:NotBlank(message = "Bucket must be provided") val bucket: String,
        val path: String? = ""
    )

    @Serializable
    @Resource("{instanceId}/fs/{bucket}/file/read")
    internal class FSReadFile(
        @Suppress("UNUSED") val parent: InstanceRoutes = InstanceRoutes(),
        @field:MustBeSnowflake val instanceId: String,
        @field:NotBlank(message = "Bucket must be provided") val bucket: String,
        @field:NotBlank(message = "File absolute path must be provided") val path: String? = null,
        @SerialName("start") val startIndex: Int? = null,
        @SerialName("end") val endIndex: Int? = null
    )
}
