/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    val lastCpuUsage: Long?
    val lastPerCpuUsage: LongArray?
    val lastSystemCpuUsage: Long?
    val lastOnlineCpus: Long?

}

fun ServerStats.getUsedMemory(): Long {
    return memoryUsage - memoryCache
}

fun ServerStats.getMemoryUsagePercentage(): Float {
    return (getUsedMemory().toFloat() / memoryLimit.toFloat()) * 100.0F
}

fun ServerStats.getCpuUsagePercentage(): Float {
    if (lastCpuUsage == null || lastSystemCpuUsage == null)
        return 0.0F

    return ((cpuUsage - lastCpuUsage!!).toFloat() / (systemCpuUsage - lastSystemCpuUsage!!).toFloat()) * onlineCpus * 100.0F
}

fun ServerStats.getCpuUsagePercentage(usage: Long): Float {
    return (usage.toFloat() / cpuUsage) * 100.0F
}