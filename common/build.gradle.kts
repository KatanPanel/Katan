dependencies {
    val log4jVersion = "2.13.3"

    api(project(":api"))
    api("com.typesafe:config:1.4.0")
    api("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT")
    api("org.apache.logging.log4j:log4j-api:$log4jVersion")
    api("org.apache.logging.log4j:log4j-core:$log4jVersion")
    api("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    api("redis.clients:jedis:3.3.0")
}