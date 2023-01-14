package org.katan.service.id

interface IdService {

    suspend fun generate(): Long

    suspend fun parse(input: String): Long
}
