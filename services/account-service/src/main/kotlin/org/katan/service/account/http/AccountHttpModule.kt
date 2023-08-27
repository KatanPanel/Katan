package org.katan.service.account.http

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.service.account.http.routes.listAccounts
import org.katan.service.account.http.routes.register

internal class AccountHttpModule : HttpModule() {

    override fun install(app: Application) {
        app.routing {
            authenticate {
                listAccounts()
                register()
            }
        }
    }
}
