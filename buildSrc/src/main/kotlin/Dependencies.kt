object Dependencies {

    const val kotlinVersion = "1.4.21"

    object Coroutines {
        private const val prefix = "org.jetbrains.kotlinx:kotlinx-coroutines"
        private const val version = "1.4.2"

        const val coreArtifact = "$prefix-core:$version"
    }

    object Serialization {
        private const val prefix = "org.jetbrains.kotlinx:kotlinx-serialization"
        private const val version = "1.0.0-RC2"

        const val jsonArtifact = "$prefix-json:$version"
    }

    object Exposed {
        private const val prefix = "org.jetbrains.exposed:exposed"
        private const val version = "0.27.1"

        const val core = "$prefix-core:$version"
        const val dao = "$prefix-dao:$version"
        const val jdbc = "$prefix-jdbc:$version"
        const val javaTime = "$prefix-java-time:$version"
    }

    object AtomicFU {
        private const val prefix = "org.jetbrains.kotlinx:atomicfu"
        private const val version = "0.14.4"

        const val jvmArtifact = "$prefix:$version"
    }

    object Modules {
        object Core {
            const val bouncyCastleCrypto = "org.bouncycastle:bcprov-jdk15to18:1.66"
            const val jedis = "redis.clients:jedis:3.3.0"
            const val mysqlConnector = "mysql:mysql-connector-java:8.0.21"
            const val h2Database = "com.h2database:h2:1.4.200"
            const val dockerKotlinMultiplatform = "com.github.KatanPanel.docker-kotlin-multiplatform:kotlin-docker-jvm:f25b697ad6"
        }

        object WS {
            const val javaJwt = "com.auth0:java-jwt:3.10.3"
            const val jacksonDataBind = "com.fasterxml.jackson.core:jackson-databind:2.11.2"

            object Ktor {
                private const val prefix = "io.ktor"
                private const val version = "1.4.0"

                const val serverNetty = "$prefix:ktor-server-netty:$version"
                const val serverCore = "$prefix:ktor-server-core:$version"
                const val serverTestHost = "$prefix:ktor-server-test-host:$version"
                const val authJwt = "$prefix:ktor-auth-jwt:$version"
                const val websockets = "$prefix:ktor-websockets:$version"
                const val locations = "$prefix:ktor-locations:$version"
                const val jackson = "$prefix:ktor-jackson:$version"
                const val networkTls = "$prefix:ktor-network-tls-certificates:$version"
            }
        }
    }

}