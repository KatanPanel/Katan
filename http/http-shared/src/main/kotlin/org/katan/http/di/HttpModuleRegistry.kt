package org.katan.http.di

import java.util.Collections

class HttpModuleRegistry : Iterable<HttpModule> {

    private val modules: MutableList<HttpModule> = mutableListOf()

    fun register(module: HttpModule) {
        modules.add(module)
    }

    override fun iterator(): Iterator<HttpModule> {
        return Collections.unmodifiableList(modules).iterator()
    }
}
