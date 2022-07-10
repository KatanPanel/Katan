subprojects {
    dependencies {
        implementation(project(":model"))
    }

    kotlin {
        explicitApi()
    }
}