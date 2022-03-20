package org.katan

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.factory.ServerFactory
import org.katan.maestro.Maestro
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.Closeable

/**
 * Katan's core class.
 *
 * @constructor Constructs a new Katan Core instance.
 */
class Katan : KoinComponent, Closeable {
	
	companion object {
		val LOGGER: Logger = LogManager.getLogger(Katan::class.java)
	}
	
	private val maestro: Maestro = get()
	val serverFactory: ServerFactory = get()
	
	fun start() {
		LOGGER.info("Katan started")
	}
	
	override fun close() {
		maestro.close()
		LOGGER.info("Katan shutdown")
	}
	
}