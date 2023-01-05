package org.katan.http

class HttpModuleRegistry {

    private val _modules: MutableSet<HttpModule> = hashSetOf()
    val modules: Set<HttpModule> get() = _modules

    operator fun plusAssign(module: HttpModule) {
        _modules.add(module)
    }
}
