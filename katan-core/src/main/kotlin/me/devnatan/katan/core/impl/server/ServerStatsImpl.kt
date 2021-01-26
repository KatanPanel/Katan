package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.ServerStats

class ServerStatsImpl(
    override val pid: Long,
    override val memoryUsage: Long,
    override val memoryMaxUsage: Long,
    override val memoryLimit: Long,
    override val memoryCache: Long,
    override val cpuUsage: Long,
    override val perCpuUsage: LongArray,
    override val systemCpuUsage: Long,
    override val onlineCpus: Long,
    override val lastCpuUsage: Long,
    override val lastPerCpuUsage: LongArray,
    override val lastSystemCpuUsage: Long,
    override val lastOnlineCpus: Long
) : ServerStats