package org.katan.service.instance.model

import org.katan.model.instance.InstanceInternalStats

data class InstanceInternalStatsImpl(
    override val pid: Long,
    override val memoryUsage: Long,
    override val memoryMaxUsage: Long,
    override val memoryLimit: Long,
    override val memoryCache: Long,
    override val cpuUsage: Long,
    @Suppress("ArrayInDataClass") override val perCpuUsage: LongArray,
    override val systemCpuUsage: Long,
    override val onlineCpus: Long,
    override val lastCpuUsage: Long?,
    @Suppress("ArrayInDataClass") override val lastPerCpuUsage: LongArray?,
    override val lastSystemCpuUsage: Long?,
    override val lastOnlineCpus: Long?,
) : InstanceInternalStats
