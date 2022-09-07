package org.katan.model.instance

interface InstanceInternalStats {

    val pid: Long
    val memoryUsage: Long
    val memoryMaxUsage: Long
    val memoryLimit: Long
    val memoryCache: Long
    val cpuUsage: Long
    val perCpuUsage: LongArray
    val systemCpuUsage: Long
    val onlineCpus: Long
    val lastCpuUsage: Long?
    val lastPerCpuUsage: LongArray?
    val lastSystemCpuUsage: Long?
    val lastOnlineCpus: Long?

}

fun InstanceInternalStats.getMemoryUsagePercentage(): Float {
    val usedMemory = memoryUsage - memoryCache
    return (usedMemory.toFloat() / memoryLimit.toFloat()) * 100.0F
}

fun InstanceInternalStats.getCpuUsagePercentage(): Float {
    if (lastCpuUsage == null || lastSystemCpuUsage == null)
        return 0.0F

    val cpuUsageDiff = cpuUsage - lastCpuUsage!!
    val sysCpuUsageDiff = systemCpuUsage - lastSystemCpuUsage!!

    return (cpuUsageDiff.toFloat() / sysCpuUsageDiff.toFloat()) * onlineCpus * 100.0F
}

fun InstanceInternalStats.getCpuUsagePercentage(usage: Long): Float {
    return (usage.toFloat() / cpuUsage) * 100.0F
}