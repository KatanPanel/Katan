@file:JvmName("Application")

package org.katan

import org.katan.Katan.Companion.LOGGER
import org.katan.maestro.di.MaestroModule
import org.koin.core.context.startKoin

fun main() {
	startKoin {
		logger(KoinLog4jLogger(LOGGER))
		modules(MaestroModule)
	}
	
	// starts Katan application
	val app = Katan()
	app.start()
	
	Runtime.getRuntime().addShutdownHook(Thread {
		app.close()
	})
}