package org.katan.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.okhttp.OkDockerHttpClient
import org.katan.config.KatanConfig

class DockerClientInitializer(
    private val config: KatanConfig
) {

    /**
     * Initializes a [DockerClient] with config host as Docker host.
     */
    fun init(): DockerClient {
        val config = DefaultDockerClientConfig.Builder()
            .withDockerHost(config.docker.host)
            .build()

        return DockerClientImpl.getInstance(
            config,
            OkDockerHttpClient.Builder()
                .dockerHost(config.dockerHost)
                .build()
        )
    }
}
