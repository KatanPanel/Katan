package me.devnatan.katan.api.server

interface ServerStats {

    val memoryUsage: Long
    val memoryMaxUsage: Long
    val memoryLimit: Long

    val cpuUsage: Long
    val totalCpuUsage: Long
    val systemCpuUsage: Long
    val onlineCpus: Long

}