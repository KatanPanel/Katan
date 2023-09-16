package org.katan.service.id

import de.mkammerer.snowflakeid.SnowflakeIdGenerator
import org.katan.KatanConfig
import org.katan.model.Snowflake
import org.katan.model.toSnowflake
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class SnowflakeIdServiceImpl(config: KatanConfig) : IdService {

    private val generator = SnowflakeIdGenerator.createDefault(config.nodeId)

    override suspend fun generate(): Snowflake {
        val generated = suspendCoroutine { cont ->
            runCatching { generator.next() }
                .onSuccess(cont::resume)
                .onFailure(cont::resumeWithException)
        }

        return generated.toSnowflake()
    }

    override suspend fun parse(input: String): Snowflake {
        // TODO check if it's a valid snowflake id
        return input.toLong().toSnowflake()
    }
}
