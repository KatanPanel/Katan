package org.katan.http.di

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.Collections

class HttpModuleRegistry : Iterable<HttpModule> {

    private val logger: Logger = LogManager.getLogger(HttpModuleRegistry::class.java)

    private val modules: MutableList<HttpModule> = mutableListOf()

    fun register(module: HttpModule) {
        modules.add(module)
        logger.info("Module {} registered", module::class.simpleName)
    }

    override fun iterator(): Iterator<HttpModule> {
        return Collections.unmodifiableList(modules).iterator()
    }
}
