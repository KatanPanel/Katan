repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":data-types"))
    implementation(rootProject.libs.yoki)
}