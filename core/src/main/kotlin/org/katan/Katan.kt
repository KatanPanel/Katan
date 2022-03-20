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
		private const val DEFAULT_PORT: Int = 40055
		val LOGGER: Logger = LogManager.getLogger(Katan::class.java)
	}
	
	private val maestro: Maestro = get()
	val serverFactory: ServerFactory = get()
	val port by lazy { selectPort() }
	
	fun start() {
		LOGGER.info("Katan started on $port")
	}
	
	override fun close() {
		maestro.close()
		LOGGER.info("Katan shutdown")
	}

	/**
	 * Gets the port that serve will be run on from the environment variable
	 * or use the [DEFAULT_PORT] if the port environment variable is not defined
	 */
	private fun selectPort(): Int {
		return System.getenv("PORT").toIntOrNull() ?: DEFAULT_PORT
	}
	
}