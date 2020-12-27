package me.devnatan.katan.api.server

interface ServerStats {

    val pid: Long

    val memoryUsage: Long
    val memoryMaxUsage: Long
    val memoryLimit: Long
    val memoryCache: Long

    val cpuUsage: Long
    val perCpuUsage: LongArray
    val systemCpuUsage: Long
    val onlineCpus: Long

    val lastCpuUsage: Long
    val lastPerCpuUsage: LongArray
    val lastSystemCpuUsage: Long
    val lastOnlineCpus: Long

}

fun ServerStats.getUsedMemory(): Long {
    return memoryUsage - memoryCache
}

fun ServerStats.getMemoryUsagePercentage(): Float {
    return (getUsedMemory().toFloat() / memoryLimit.toFloat()) * 100.0F
}

fun ServerStats.getCpuUsagePercentage(): Float {
    return ((cpuUsage - lastCpuUsage).toFloat() / (systemCpuUsage - lastSystemCpuUsage).toFloat()) * onlineCpus * 100.0F
}

fun ServerStats.getCpuUsagePercentage(usage: Long): Float {
    return (usage.toFloat() / cpuUsage) * 100.0F
}