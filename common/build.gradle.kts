repositories {
    maven("http://nexus.devsrsouza.com.br/repository/maven-public/") // eventkt-core-jvm
}

dependencies {
    api(project(":api"))
    api("com.typesafe:config:1.4.0")
    api("br.com.devsrsouza.eventkt:eventkt-core-jvm:0.1.0-SNAPSHOT")
}