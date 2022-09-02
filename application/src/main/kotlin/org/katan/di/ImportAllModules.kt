package org.katan.di

import org.katan.config.di.configDI
import org.katan.crypto.di.cryptoDI
import org.katan.docker.di.dockerClientDI
import org.katan.event.di.eventsDispatcherDI
import org.katan.http.client.di.httpClientDI
import org.katan.http.server.di.httpServerDI
import org.katan.service.account.di.accountServiceDI
import org.katan.service.auth.di.authServiceDI
import org.katan.service.db.di.databaseServiceDI
import org.katan.service.dockerInstance.di.dockerInstanceServiceImplDI
import org.katan.service.dockerNetwork.di.dockerNetworkServiceDI
import org.katan.service.fs.host.di.hostFsServiceDI
import org.katan.service.id.di.idServiceDI
import org.katan.service.instance.di.unitInstanceServiceDI
import org.katan.service.unit.di.unitServiceDI
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
        dockerInstanceServiceImplDI,
        cacheServiceDI,
        databaseServiceDI,
        dockerClientDI,
        hostFsServiceDI,
        httpClientDI
    )
}
