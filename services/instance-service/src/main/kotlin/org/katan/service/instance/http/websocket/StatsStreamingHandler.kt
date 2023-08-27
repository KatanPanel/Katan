package org.katan.service.instance.http.websocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.http.websocket.WebSocketPacket.Companion.TARGET_ID
import org.katan.http.websocket.WebSocketPacket.Companion.VALUE
import org.katan.http.websocket.WebSocketPacketContext
import org.katan.http.websocket.WebSocketPacketEventHandler
import org.katan.http.websocket.respond
import org.katan.http.websocket.stringData
import org.katan.model.instance.getCpuUsagePercentage
import org.katan.model.instance.getMemoryUsagePercentage
import org.katan.model.toSnowflake
import org.katan.service.instance.InstanceService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Serializable
private data class StatsStreamingResponse(
    @SerialName(VALUE) val value: InternalStatsResponse,
)

@Suppress("ArrayInDataClass")
@Serializable
private data class InternalStatsResponse(
    @SerialName("mem-usage") val memoryUsage: Long,
    @SerialName("mem-usage-pc") val memoryUsagePercent: Float,
    @SerialName("mem-max-usage") val memoryMaxUsage: Long,
    @SerialName("mem-limit") val memoryLimit: Long,
    @SerialName("mem-cache") val memoryCache: Long,
    @SerialName("cpus") val onlineCpus: Long,
    @SerialName("last-cpus") val lastOnlineCpus: Long?,
    @SerialName("cpu") val cpuUsage: Long,
    @SerialName("cpu-usage-pc") val cpuUsagePercent: Float,
    @SerialName("last-cpu") val lastCpuUsage: Long?,
    @SerialName("sys-cpu") val systemCpuUsage: Long,
    @SerialName("last-sys-cpu") val lastSystemCpuUsage: Long?,
    @SerialName("per-cpu") val perCpuUsage: LongArray?,
    @SerialName("per-cpu-pc") val perCpuUsagePercent: FloatArray,
    @SerialName("last-per-cpu") val lastPerCpuUsage: LongArray?,
)

internal class StatsStreamingHandler :
    WebSocketPacketEventHandler(), KoinComponent {

    private val instanceService by inject<InstanceService>()

    override suspend fun WebSocketPacketContext.handle() {
        val target = stringData(TARGET_ID)?.toLongOrNull()?.toSnowflake() ?: return

        instanceService.streamInternalStats(target).collect { stats ->
            with(stats) {
                respond(
                    StatsStreamingResponse(
                        InternalStatsResponse(
                            memoryUsage = memoryUsage,
                            memoryUsagePercent = stats.getMemoryUsagePercentage(),
                            memoryMaxUsage = memoryMaxUsage,
                            memoryLimit = memoryLimit,
                            memoryCache = memoryCache,
                            onlineCpus = onlineCpus,
                            lastOnlineCpus = lastOnlineCpus,
                            cpuUsage = cpuUsage,
                            cpuUsagePercent = getCpuUsagePercentage(),
                            lastCpuUsage = lastCpuUsage,
                            systemCpuUsage = systemCpuUsage,
                            lastSystemCpuUsage = lastSystemCpuUsage,
                            perCpuUsage = perCpuUsage,
                            perCpuUsagePercent = perCpuUsage.map { stats.getCpuUsagePercentage(it) }
                                .toFloatArray(),
                            lastPerCpuUsage = lastPerCpuUsage,
                        ),
                    ),
                )
            }
        }
    }
}
