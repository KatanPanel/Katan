plugins {
	application
	alias(libs.plugins.shadowjar)
}

application {
	mainClass.set("org.katan.Application")
}

dependencies {
	implementation(project(":core"))
	implementation(project(":runtime"))
	implementation(project(":services:id-service"))
	implementation(project(":services:account-service"))
	implementation(project(":services:unit-service"))
	implementation(project(":services:unit-instance-service"))
}

tasks {
	jar {
		manifest {
			attributes["Main-Class"] = application.mainClass.get()
			attributes["Implementation-Version"] = project.version
		}
	}
}