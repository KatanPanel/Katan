package org.katan.service.server

import org.katan.service.container.Container

public interface Server {

    public val id: String

    public val name: String

    public val container: Container

}