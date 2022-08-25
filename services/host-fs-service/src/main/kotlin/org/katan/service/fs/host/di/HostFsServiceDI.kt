package org.katan.service.fs.host.di

import org.katan.service.fs.FSService
import org.katan.service.fs.host.HostFSService
import org.koin.core.module.Module
import org.koin.dsl.module

public val hostFsServiceDI: Module = module {
    single<FSService> { HostFSService(get()) }
}