plugins {
	application
}

application {
	mainClass.set("org.katan.Application")
}

dependencies {
	implementation(project(":core"))
	implementation(project(":runtime"))

	implementation(project(":services:service-container"))
	implementation(project(":services:service-server"))
}

tasks {
	jar {
		manifest {
			attributes["Main-Class"] = application.mainClass.get()
			attributes["Implementation-Version"] = project.version
		}
	}
}