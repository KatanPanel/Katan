package org.katan.service.server

public interface ServerService {

    public suspend fun get(id: String): Server?

    public suspend fun create(options: ServerCreateOptions): Server

}