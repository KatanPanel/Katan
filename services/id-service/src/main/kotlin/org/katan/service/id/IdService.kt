package org.katan.service.id

import org.katan.model.Snowflake

public interface IdService {

    public suspend fun generate(): Snowflake

    public suspend fun parse(input: String): Snowflake
}
