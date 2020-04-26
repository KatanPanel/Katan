package me.devnatan.katan.core.impl.server

import me.devnatan.katan.api.server.KServer
import me.devnatan.katan.api.server.KServerContainer
import me.devnatan.katan.api.server.KServerQuery

class KServerImpl(
    override val id: UInt,
    override val name: String,
    override val port: Short,
    override val container: KServerContainer,
    override var query: KServerQuery
) : KServer