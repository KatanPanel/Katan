package org.katan.service.id

public interface IdService {

    public suspend fun generate(): Long

}