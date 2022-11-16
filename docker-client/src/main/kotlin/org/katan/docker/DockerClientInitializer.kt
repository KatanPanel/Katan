package org.katan.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.okhttp.OkDockerHttpClient
import org.katan.config.KatanConfig
import org.katan.yoki.Yoki

class DockerClientInitializer(
    private val config: KatanConfig
) {

    /**
     * Initializes a [DockerClient] with config host as Docker host.
     */
    fun initClient(): DockerClient {
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

    fun initYoki(): Yoki {
        // TODO apply docker host based on configuration if available
        return Yoki { forCurrentPlatform() }
    }
}
