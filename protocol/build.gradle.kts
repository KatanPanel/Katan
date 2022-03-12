dependencies {
    runtimeOnly(libs.grpc.netty)
    testImplementation(libs.grpc.testing)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.kotlin.stub)
}