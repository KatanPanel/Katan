subprojects {
    dependencies {
        api(project(":model"))
    }

    kotlin {
        explicitApi()
    }
}