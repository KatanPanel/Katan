package org.katan.service.id

import de.mkammerer.snowflakeid.SnowflakeIdGenerator
import kotlin.coroutines.suspendCoroutine

internal class SnowflakeIdServiceImpl : IdService {

    private val generator = SnowflakeIdGenerator.createDefault(getGeneratorId())

    private fun getGeneratorId(): Int {
        val envVar = System.getenv("KT_GEN_ID")
        return envVar?.toIntOrNull() ?: 0
    }

    override suspend fun generate(): Long {
        return suspendCoroutine {
            it.resumeWith(runCatching { generator.next() })
        }
    }

}