package org.katan.di

import org.katan.config.di.configDI
import org.katan.crypto.di.cryptoDI
import org.katan.docker.di.dockerClientDI
import org.katan.event.di.eventsDispatcherDI
import org.katan.http.server.di.httpServerDI
import org.katan.service.account.di.accountServiceDI
import org.katan.service.auth.di.authServiceDI
import org.katan.service.db.di.databaseServiceDI
import org.katan.service.docker.network.di.dockerNetworkServiceDI
import org.katan.service.fs.host.di.hostFsServiceDI
import org.katan.service.id.di.idServiceDI
import org.katan.service.server.di.unitServiceDI
import org.katan.service.unit.instance.di.unitInstanceServiceDI
import org.katan.service.unit.instance.docker.di.dockerUnitInstanceServiceImplDI
import org.katan.services.cache.di.cacheServiceDI
import org.koin.core.KoinApplication

internal fun KoinApplication.importAllModules() {
    modules(
        configDI,
        cryptoDI,
        httpServerDI,
        eventsDispatcherDI,
        idServiceDI,
        authServiceDI,
        accountServiceDI,
        unitServiceDI,
        unitInstanceServiceDI,
        dockerNetworkServiceDI,
        dockerUnitInstanceServiceImplDI,
        cacheServiceDI,
        databaseServiceDI,
        dockerClientDI,
        hostFsServiceDI
    )
}
