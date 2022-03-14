plugins {
	application
}

application {
	mainClass.set("org.katan.Application")
}

dependencies {
	implementation(libs.log4j.core)
}

tasks {
	jar {
		manifest {
			attributes["Main-Class"] = application.mainClass.get()
			attributes["Implementation-Version"] = project.version
		}
	}
}