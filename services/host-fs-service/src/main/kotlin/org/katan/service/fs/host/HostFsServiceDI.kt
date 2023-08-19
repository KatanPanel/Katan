package org.katan.service.fs.host

import org.katan.service.fs.FSService
import org.katan.service.fs.host.HostFSService
import org.koin.core.module.Module
import org.koin.dsl.module

public val hostFsServiceDI: Module = module {
    single<FSService> {
        HostFSService(
            dockerClient = get(),
            config = get()
        )
    }
}
