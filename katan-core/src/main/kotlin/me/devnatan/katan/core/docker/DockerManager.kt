package me.devnatan.katan.core.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.core.KeystoreSSLConfig
import com.github.dockerjava.core.LocalDirectorySSLConfig
import com.github.dockerjava.okhttp.OkDockerHttpClient
import me.devnatan.katan.api.isWindows
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.common.EnvKeys
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.getEnv
import me.devnatan.katan.core.KatanCore
import org.slf4j.Logger
import java.security.KeyStore
import kotlin.system.exitProcess

class DockerManager(private val core: KatanCore) {

    private companion object {
        val logger: Logger = logger<DockerManager>()
    }

    lateinit var client: DockerClient

    fun initialize() {
        logger.info(core.translator.translate("katan.docker.config"))
        val dockerConfig = core.config.getConfig("docker")
        val host = dockerConfig.getEnv("host", EnvKeys.DOCKER_URI)!!
        if (host.startsWith("unix") && core.platform.isWindows()) {
            logger.error(core.translator.translate("katan.docker.unix-domain-sockets", host))
            exitProcess(0)
        }

        val tls = dockerConfig.get("tls.verify", false)
        val clientConfigBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(host)
            .withDockerTlsVerify(tls)

        if (tls) {
            logger.info(core.translator.translate("katan.docker.tls-enabled"))
            clientConfigBuilder.withDockerCertPath(dockerConfig.getString("tls.certPath"))
        } else
            logger.warn(
                core.translator.translate("katan.docker.tls-disabled")
            )

        if (dockerConfig.get("ssl.enabled", false)) {
            clientConfigBuilder.withCustomSslConfig(
                when (dockerConfig.getString("ssl.provider")) {
                    "CERT" -> {
                        val path = dockerConfig.getString("ssl.certPath")
                        logger.info(core.translator.translate("katan.docker.cert-loaded", path))
                        LocalDirectorySSLConfig(path)
                    }
                    "KEY_STORE" -> {
                        val type = dockerConfig.get("keyStore.provider") ?: KeyStore.getDefaultType()
                        val keystore = KeyStore.getInstance(type)
                        logger.info(core.translator.translate("katan.docker.ks-loaded", type))

                        KeystoreSSLConfig(keystore, dockerConfig.getString("keyStore.password"))
                    }
                    else -> throw IllegalArgumentException("Unrecognized Docker SSL provider. Must be: CERT or KEY_STORE")
                }
            )
        }

        val properties = dockerConfig.getConfig("properties")
        val clientConfig = clientConfigBuilder.build()

        client = DockerClientImpl.getInstance(
            clientConfig, OkDockerHttpClient.Builder()
                .dockerHost(clientConfig.dockerHost)
                .sslConfig(clientConfig.sslConfig)
                .connectTimeout(properties.get("connectTimeout", 60000))
                .readTimeout(properties.get("readTimeout", 60000))
                .build()
        )

        // sends a ping to see if the connection will be established.
        client.pingCmd().exec()
        logger.info(core.translator.translate("katan.docker.ready"))
    }

}