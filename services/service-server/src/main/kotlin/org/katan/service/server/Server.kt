package org.katan.service.server

import org.katan.service.container.Container
import kotlinx.serialization.Serializable

@Serializable
public interface Server {

    public val id: String

    public val name: String

    public val container: Container

}