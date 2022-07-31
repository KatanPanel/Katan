package org.katan.di

import org.katan.config.di.configDI
import org.katan.event.di.eventsDispatcherDI
import org.katan.http.server.di.httpServerDI
import org.katan.service.account.di.accountServiceDI
import org.katan.service.auth.di.authServiceDI
import org.katan.service.db.di.databaseServiceDI
import org.katan.service.id.di.idServiceDI
import org.katan.service.server.di.unitServiceDI
import org.katan.service.unit.instance.docker.di.dockerUnitInstanceServiceImplDI
import org.katan.services.cache.di.cacheServiceDI
import org.koin.core.KoinApplication

internal fun KoinApplication.importAllModules() {
    modules(
        configDI,
        httpServerDI,
        eventsDispatcherDI,
        idServiceDI,
        authServiceDI,
        accountServiceDI,
        unitServiceDI,
        dockerUnitInstanceServiceImplDI,
        cacheServiceDI,
        databaseServiceDI
    )
}
