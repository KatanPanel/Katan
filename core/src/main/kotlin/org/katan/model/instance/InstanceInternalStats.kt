package org.katan.model.instance

public interface InstanceInternalStats {

    public val pid: Long
    public val memoryUsage: Long
    public val memoryMaxUsage: Long
    public val memoryLimit: Long
    public val memoryCache: Long
    public val cpuUsage: Long
    public val perCpuUsage: LongArray
    public val systemCpuUsage: Long
    public val onlineCpus: Long
    public val lastCpuUsage: Long?
    public val lastPerCpuUsage: LongArray?
    public val lastSystemCpuUsage: Long?
    public val lastOnlineCpus: Long?
}

private const val MAX_PERCENTAGE = 100.0f

public fun InstanceInternalStats.getMemoryUsagePercentage(): Float {
    val usedMemory = memoryUsage - memoryCache
    return (usedMemory.toFloat() / memoryLimit.toFloat()) * MAX_PERCENTAGE
}

public fun InstanceInternalStats.getCpuUsagePercentage(): Float {
    if (lastCpuUsage == null || lastSystemCpuUsage == null) {
        return 0.0F
    }

    val cpuUsageDiff = cpuUsage - lastCpuUsage!!
    val sysCpuUsageDiff = systemCpuUsage - lastSystemCpuUsage!!

    return (cpuUsageDiff.toFloat() / sysCpuUsageDiff.toFloat()) * onlineCpus * MAX_PERCENTAGE
}

public fun InstanceInternalStats.getCpuUsagePercentage(usage: Long): Float {
    return (usage.toFloat() / cpuUsage) * MAX_PERCENTAGE
}
