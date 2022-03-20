package org.katan

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.maestro.di.MaestroModule
import org.katan.runtime.di.RuntimeModule
import org.koin.core.context.startKoin

@Suppress("UNUSED")
private object Application {
	
	val LOGGER: Logger = LogManager.getLogger(Application::class.java)
	
	@JvmStatic
	fun main(args: Array<String>) {
		startKoin {
			logger(KoinLog4jLogger())
			modules(MaestroModule, RuntimeModule)
		}
		
		val app = Katan()
		Runtime.getRuntime().addShutdownHook(Thread({
			app.close()
		}, "Katan Shutdown"))
		
		app.start()
	}
	
}