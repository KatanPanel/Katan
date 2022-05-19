package org.katan.service.container

public interface ContainerFactory {

    public suspend fun generateId(): String

    public suspend fun create(options: ContainerCreateOptions): Container

}