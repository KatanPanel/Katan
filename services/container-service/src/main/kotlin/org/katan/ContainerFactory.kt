package org.katan

public interface ContainerFactory {

    public suspend fun create(options: ContainerCreateOptions): Container

}