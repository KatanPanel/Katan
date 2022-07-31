package org.katan.service.id

import de.mkammerer.snowflakeid.SnowflakeIdGenerator
import org.katan.config.KatanConfig
import kotlin.coroutines.suspendCoroutine

internal class SnowflakeIdServiceImpl(
    config: KatanConfig
) : IdService {

    private val generator = SnowflakeIdGenerator.createDefault(config.nodeId)

    override suspend fun generate(): Long {
        return suspendCoroutine {
            it.resumeWith(runCatching { generator.next() })
        }
    }

    override suspend fun parse(input: String): Long {
        // TODO check if it's a valid snowflake id
        return input.toLong()
    }
}
