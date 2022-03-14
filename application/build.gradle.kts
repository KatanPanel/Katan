plugins {
	application
}

application {
	mainClass.set("org.katan.Application")
}

tasks {
	jar {
		manifest {
			attributes["Main-Class"] = application.mainClass.get()
			attributes["Implementation-Version"] = project.version
		}
	}
}