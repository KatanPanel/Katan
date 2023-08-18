package org.katan.service.id

import org.katan.model.Snowflake

interface IdService {

    suspend fun generate(): Snowflake

    suspend fun parse(input: String): Snowflake
}
