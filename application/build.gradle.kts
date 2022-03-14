plugins {
	application
}

application {
	mainClass.set("org.katan.Application")
}

dependencies {
	implementation(project(":core"))
	implementation(project(":maestro"))
}

tasks {
	jar {
		manifest {
			attributes["Main-Class"] = application.mainClass.get()
			attributes["Implementation-Version"] = project.version
		}
	}
}