repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly(rootProject.libs.yoki)
    compileOnly(rootProject.libs.yoki.docker)
}