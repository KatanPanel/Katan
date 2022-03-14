package org.katan

import org.katan.maestro.Maestro
import java.io.Closeable

/**
 * Katan's core class.
 *
 * @constructor Constructs a new Katan Core instance.
 * @property maestro The Maestro Orchestrator to be used.
 */
class Katan(val maestro: Maestro
) : Closeable {
	
	override fun close() {
		maestro.close()
	}
	
}