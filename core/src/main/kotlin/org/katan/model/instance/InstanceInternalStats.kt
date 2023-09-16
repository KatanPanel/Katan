package org.katan.model.instance

data class InstanceInternalStats(
    val pid: Long,
    val memoryUsage: Long,
    val memoryMaxUsage: Long,
    val memoryLimit: Long,
    val memoryCache: Long,
    val cpuUsage: Long,
    @Suppress("ArrayInDataClass") val perCpuUsage: LongArray,
    val systemCpuUsage: Long,
    val onlineCpus: Long,
    val lastCpuUsage: Long?,
    @Suppress("ArrayInDataClass") val lastPerCpuUsage: LongArray?,
    val lastSystemCpuUsage: Long?,
    val lastOnlineCpus: Long?,
)

private const val MAX_PERCENTAGE = 100.0f

fun InstanceInternalStats.getMemoryUsagePercentage(): Float {
    val usedMemory = memoryUsage - memoryCache
    return (usedMemory.toFloat() / memoryLimit.toFloat()) * MAX_PERCENTAGE
}

fun InstanceInternalStats.getCpuUsagePercentage(): Float {
    if (lastCpuUsage == null || lastSystemCpuUsage == null) {
        return 0.0F
    }

    val cpuUsageDiff = cpuUsage - lastCpuUsage
    val sysCpuUsageDiff = systemCpuUsage - lastSystemCpuUsage

    return (cpuUsageDiff.toFloat() / sysCpuUsageDiff.toFloat()) * onlineCpus * MAX_PERCENTAGE
}

fun InstanceInternalStats.getCpuUsagePercentage(usage: Long): Float {
    return (usage.toFloat() / cpuUsage) * MAX_PERCENTAGE
}
