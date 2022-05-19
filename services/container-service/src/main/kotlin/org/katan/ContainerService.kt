package org.katan

interface ContainerService {

    suspend fun getContainer(id: String): Container

    suspend fun createContainer(options: ContainerCreateOptions)

}