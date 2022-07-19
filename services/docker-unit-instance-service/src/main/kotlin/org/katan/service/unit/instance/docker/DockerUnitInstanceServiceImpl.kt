package org.katan.service.unit.instance.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.katan.config.KatanConfig
import org.katan.model.unit.UnitInstance
import org.katan.service.network.NetworkService
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.UnitInstanceSpec
import java.io.Closeable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.jvmName

internal class DockerUnitInstanceServiceImpl(
    private val config: KatanConfig,
    private val networkService: NetworkService
) : UnitInstanceService,
    CoroutineScope by CoroutineScope(
        CoroutineName(DockerUnitInstanceServiceImpl::class.jvmName)
    ) {

    private companion object {
        private val logger = LogManager.getLogger(DockerUnitInstanceServiceImpl::class.java)
    }

    private val client: DockerClient by lazy { initClient() }

    override fun fromSpec(data: Map<String, Any>): UnitInstanceSpec {
        check(data.containsKey(IMAGE_PROPERTY)) { "Missing required property \"$IMAGE_PROPERTY\"." }
        return DockerUnitInstanceSpec(data.getValue(IMAGE_PROPERTY) as String)
    }

    override suspend fun createInstanceFor(spec: UnitInstanceSpec): UnitInstance {
        require(spec is DockerUnitInstanceSpec) { "Instance spec must be a DockerUnitInstanceSpec" }

        logger.info("Generating a unit instance: $spec")
        return coroutineScope {
            // TODO better context switch
            val id = withContext(Dispatchers.IO) {
                tryCreateContainer(spec.image)
            }

            logger.info("Unit instance generated successfully: $id")
            val container = withContext(Dispatchers.IO) { client.inspectContainerCmd(id).exec() }

            logger.info("Generated instance name: ${container.name}")
            val connection =
                networkService.createUnitConnection(container.networkSettings.ipAddress, 8080)

            UnitInstance(connection)
        }
    }

    private suspend fun tryCreateContainer(image: String): String {
        return try {
            createContainer(image)
        } catch (e: NotFoundException) {
            pullContainerImage(image)
            createContainer(image)
        }
    }

    private suspend fun createContainer(image: String): String =
        suspendCoroutine<CreateContainerResponse> { cont ->
            cont.resumeWith(runCatching {
                client.createContainerCmd(image).exec()
            })
        }.id

    private suspend fun pullContainerImage(image: String) =
        suspendCancellableCoroutine<Unit> { cont ->
            client.pullImageCmd(image).exec(object : PullImageResultCallback() {
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

    private fun initClient(): DockerClient {
        return DockerClientBuilder.getInstance(
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(config.docker.host)
                .build()
        )
            .build()
    }

}