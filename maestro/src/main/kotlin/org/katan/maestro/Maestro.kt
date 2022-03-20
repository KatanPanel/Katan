package org.katan.maestro

import org.katan.Server
import java.io.Closeable

/**
 * Maestro is what we call Katan's orchestrator, he is the one who takes care of container management of any entity
 * that has a container attached to it, he is the only module that has access to the Container Engine Runtime.
 */
interface Maestro : Closeable {

    suspend fun createServer(server: Server): Server

}