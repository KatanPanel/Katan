import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.protobuf)
}

dependencies {
//    runtimeOnly(libs.grpc.netty)
    testImplementation(libs.grpc.testing)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.kotlin.stub)
    protobuf(project(":grpc:protos"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${libs.versions.grpcKotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}