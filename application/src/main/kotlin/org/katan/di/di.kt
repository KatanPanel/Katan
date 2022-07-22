package org.katan.di

import org.katan.config.di.configDI
import org.katan.event.di.eventsDispatcherDI
import org.katan.http.server.di.httpServerDI
import org.katan.service.account.di.accountServiceDI
import org.katan.service.id.di.idServiceDI
import org.katan.service.network.di.networkServiceDI
import org.katan.service.server.di.unitServiceDI
import org.katan.service.unit.instance.di.unitInstanceServiceDI
import org.katan.service.unit.instance.docker.di.dockerUnitInstanceServiceImplDI
import org.koin.core.KoinApplication

internal fun KoinApplication.importAllModules() {
    modules(
        configDI,
        httpServerDI,
        eventsDispatcherDI,
        idServiceDI,
        networkServiceDI,
        accountServiceDI,
        unitServiceDI,
        unitInstanceServiceDI,
        dockerUnitInstanceServiceImplDI
    )
}