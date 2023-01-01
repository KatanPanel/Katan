package org.katan.docker.di

import org.katan.docker.DockerClientInitializer
import org.koin.core.module.Module
import org.koin.dsl.module

val dockerClientDI: Module = module {
    single { DockerClientInitializer(get()).init() }
}
