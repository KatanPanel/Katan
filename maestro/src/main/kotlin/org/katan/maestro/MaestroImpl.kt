package org.katan.maestro

import org.katan.Server
import org.katan.maestro.runtime.ServerFactory
import org.katan.maestro.spec.ServerSpec

class MaestroImpl(val katan: Katan) : Maestro {

	override suspend fun createServer(spec: ServerSpec): Server {
		TODO("Not yet implemented")
	}

	override fun close() {
	}
}