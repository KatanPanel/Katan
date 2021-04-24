val dockerJavaVersion = "3.2.5"

dependencies {
    api(project(":katan-common"))
    implementation(project(":katan-database"))
    implementation(project(":katan-database-jdbc"))
    implementation("org.jetbrains.kotlinx:atomicfu:0.14.4")
    implementation("com.github.docker-java:docker-java-api:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-core:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-transport-okhttp:$dockerJavaVersion")
    implementation("com.h2database:h2:1.4.200")
    implementation("mysql:mysql-connector-java:8.0.21")
    implementation("redis.clients:jedis:3.3.0")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.66")
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
    implementation("com.github.KatanPanel.docker-kotlin-multiplatform:kotlin-docker-jvm:f25b697ad6")

}

configurations {
    implementation {
        exclude(module = "bcpkix-jdk15on")
        exclude(module = "bcprov-jdk15on")
    }
}