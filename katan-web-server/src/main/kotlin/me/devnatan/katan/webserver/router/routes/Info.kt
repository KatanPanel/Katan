package me.devnatan.katan.webserver.router.routes

import io.ktor.locations.*
import io.ktor.routing.*
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.security.auth.ExternalAuthenticationProvider
import me.devnatan.katan.api.service.get
import me.devnatan.katan.webserver.KatanWS
import me.devnatan.katan.webserver.router.InfoRoute
import me.devnatan.katan.webserver.util.respondOk

@KtorExperimentalLocationsAPI
fun Route.infoRoutes(ws: KatanWS) {
    get<InfoRoute> {
        respondOk(
            "version" to Katan.VERSION,
            "version_plain" to Katan.VERSION.toString(),
            "platform" to ws.katan.platform,
            "environment" to ws.katan.environment.toString(),
            "locale" to ws.katan.translator.locale.toLanguageTag(),
            "oauth" to ws.katan.serviceManager
                .get<ExternalAuthenticationProvider>()
                .map { it.id }
        )
    }

    get<InfoRoute.Accounts> {
        respondOk("accounts" to ws.katan.accountManager.getAccounts())
    }

    get<InfoRoute.Games> {
        respondOk("games" to ws.katan.gameManager.getRegisteredGames())
    }

    get<InfoRoute.Plugins> {
        respondOk("plugins" to ws.katan.pluginManager.getPlugins())
    }

    get<InfoRoute.Permissions> {
        respondOk(
            "permissions" to ws.katan.permissionManager
                .getRegisteredPermissionKeys()
        )
    }
}