package org.katan

import kotlinx.datetime.Instant
import java.util.*

interface Server {
	
	/**
	 * The globally unique ID for this server.
	 */
	val id: UUID
	
	/**
	 * The server name.
	 * Should not be used to identify it, use [id] instead.
	 */
	val name: String
	
	/**
	 * The host address for connecting to this server.
	 */
	val hostAddress: String
	
	/**
	 * The remote port to connect to this server.
	 */
	val port: Int
	
	/**
	 * The instant this server was created.
	 */
	val createdAt: Instant
	
}