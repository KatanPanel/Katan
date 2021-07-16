val dockerJavaVersion = "3.2.5"

dependencies {
    api(project(":katan-common"))
    implementation(project(":katan-plugin-api"))
    implementation(Dependencies.AtomicFU.jvmArtifact)
    implementation(Dependencies.Modules.Core.h2Database)
    implementation(Dependencies.Modules.Core.mysqlConnector)
    implementation(Dependencies.Modules.Core.jedis)
    implementation(Dependencies.Modules.Core.bouncyCastleCrypto)
    implementation(Dependencies.Modules.Core.dockerKotlinMultiplatform)
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
    implementation("com.github.docker-java:docker-java-api:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-core:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-transport-okhttp:$dockerJavaVersion")
}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}