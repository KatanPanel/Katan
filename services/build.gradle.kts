subprojects {
    dependencies {
        api(rootProject.projects.model)
    }

    kotlin {
        explicitApi()
    }
}
