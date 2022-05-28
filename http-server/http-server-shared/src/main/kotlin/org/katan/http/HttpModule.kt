package org.katan.http

import io.ktor.server.application.Application

interface HttpModule {

    fun install(app: Application)

}