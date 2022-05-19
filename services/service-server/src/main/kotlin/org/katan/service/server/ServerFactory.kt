package org.katan.service.server

public fun interface ServerFactory {

    public suspend fun create(options: ServerCreateOptions): Server

}