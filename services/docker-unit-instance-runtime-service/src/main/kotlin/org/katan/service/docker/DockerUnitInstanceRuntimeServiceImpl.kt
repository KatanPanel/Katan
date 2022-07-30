package org.katan.service.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.model.unit.UnitInstanceStatus
import org.katan.service.docker.model.ContainerStatus
import org.katan.service.network.NetworkService
import org.katan.service.unit.instance.runtime.UnitInstanceRuntimeOptions
import org.katan.service.unit.instance.runtime.UnitInstanceRuntimeService
import org.katan.yoki.Docker
import org.katan.yoki.Yoki
import org.katan.yoki.containers
import org.katan.yoki.resource.container.remove
import java.io.Closeable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class DockerUnitInstanceRuntimeServiceImpl(
    private val config: KatanConfig,
    private val networkService: NetworkService
) : UnitInstanceRuntimeService {

    companion object {
        private val logger = LogManager.getLogger(DockerUnitInstanceRuntimeServiceImpl::class.java)
    }

    private val dockerJavaClient: DockerClient by lazy { initClient() }
    private val yokiClient: Yoki by lazy { initYokiClient() }

    override suspend fun createRuntime(options: UnitInstanceRuntimeOptions) {
        TODO("Not yet implemented")
    }

    override suspend fun removeRuntime(id: String) {
        yokiClient.containers.remove(id) {
            force = true
            removeAnonymousVolumes = true
            unlink = true
        }
    }

    override suspend fun stopRuntime(id: String) {
        yokiClient.containers.stop(id)
    }

    /**
     * Initializes a [DockerClient] with [KatanConfig.DockerClientConfig.host] as Docker host.
     */
    private fun initClient(): DockerClient {
        return DockerClientBuilder.getInstance(
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(config.docker.host)
                .build()
        ).build()
    }

    private fun initYokiClient(): Yoki {
        return Yoki(Docker)
    }

    /**
     * Tries to create a container using the given [image].
     *
     * If the given image is not available, it will pull the image and then try to create the
     * container again suspending the current coroutine for both jobs.
     * @return The created container id.
     */
    private suspend fun tryCreateContainer(image: String): String {
        return try {
            createContainer(image)
        } catch (e: NotFoundException) {
            pullContainerImage(image)
            createContainer(image)
        }
    }

    /**
     * Creates a Docker container using the given [image] suspending the coroutine until the
     * container creation workflow is completed.
     */
    private suspend fun createContainer(image: String): String =
        suspendCoroutine<CreateContainerResponse> { cont ->
            cont.resumeWith(runCatching {
                dockerJavaClient.createContainerCmd(image)
                    .buildContainerBasedOnConfiguration()
                    .exec()
            })
        }.id

    /**
     * Builds container configuration based on Katan settings.
     */
    private fun CreateContainerCmd.buildContainerBasedOnConfiguration() = apply {
//        if (config.docker.network.driver == NETWORK_DRIVER_MACVLAN)
//            applyMacvlanIpAddress()
    }

    /**
     * Pulls a Docker image from suspending the current coroutine until that image pulls completely.
     */
    private suspend fun pullContainerImage(image: String) =
        suspendCancellableCoroutine<Unit> { cont ->
            dockerJavaClient.pullImageCmd(image).exec(object : PullImageResultCallback() {
                override fun onStart(stream: Closeable?) {
                    logger.info("Preparing to pull image...")
                }

                override fun onNext(item: PullResponseItem?) {
                    logger.info("Pulling \"$image\"... $item")
                }

                override fun onError(throwable: Throwable?) {
                    cont.cancel(throwable)
                }

                override fun onComplete() {
                    logger.info("Image \"$image\" pull completed")
                    cont.resume(Unit)
                }
            })
        }

}