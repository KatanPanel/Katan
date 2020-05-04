package me.devnatan.katan

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.StreamType
import com.github.dockerjava.core.command.LogContainerResultCallback
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.websocket.webSocket
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.mapNotNull
import me.devnatan.katan.api.account.KAccount
import me.devnatan.katan.api.io.http.KHttpResponse
import me.devnatan.katan.api.io.websocket.message.KWSBaseMessage
import me.devnatan.katan.api.server.KServer
import me.devnatan.katan.api.server.KServerHolder
import me.devnatan.katan.core.util.fromString
import org.greenrobot.eventbus.EventBus
import java.io.Closeable
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

@ExperimentalUnsignedTypes
class KatanRouter(katan: Katan, router: Routing) {

    object Routes {

        const val AUTH_ENDPOINT     = "/auth"
        const val SERVERS_ENDPOINT  = "/servers"

    }

    companion object Errors {

        val INVALID_ACCESS_TOKEN      = KHttpResponse.Error(1000, "Invalid access token")

        val ACCOUNT_NOT_SPECIFIED     = KHttpResponse.Error(2000, "Username or password not specified")
        val ACCOUNT_NOT_VALID         = KHttpResponse.Error(2001, "Invalid account")
        val ACCOUNT_INVALID_USERNAME  = KHttpResponse.Error(2002, "Invalid account username")
        val ACCOUNT_WRONG_PASSWORD    = KHttpResponse.Error(2003, "Incorrect account password")
        val ACCOUNT_ALREADY_EXISTS    = KHttpResponse.Error(2004, "This account already exists")
        val ACCOUNT_NOT_FOUND         = KHttpResponse.Error(2005, "Account not found")

        val SERVER_CONFLICT           = KHttpResponse.Error(3000, "Server conflict")
        val SERVER_NOT_FOUND          = KHttpResponse.Error(3001, "Server not found")
        val SERVER_UNSPECIFIED_NOP    = KHttpResponse.Error(3002, "Server name or port not specified")
        val SERVER_HOLDER_NOT_FOUND   = KHttpResponse.Error(3003, "Server holder not found")

    }

    init {
        router.webSocket("/") {
            katan.webSocketManager.attach(this)
            try {
                incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                    val map = frame.readText().fromString(katan.jsonMapper) ?: return@consumeEach
                    EventBus.getDefault()
                        .post(KWSBaseMessage(map["id"] as String, this, map["content"]!!))
                }
            } finally {
                katan.webSocketManager.detach(this)
            }
        }

        router.route(Routes.AUTH_ENDPOINT) {
            post {
                val data = call.receive() as Map<*, *>
                if (!data.containsKey("username") || !data.containsKey("password")) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_NOT_SPECIFIED
                    )
                    return@post
                }

                val username = (data["username"]!! as String).trim()
                if (username.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_INVALID_USERNAME
                    )
                    return@post
                }

                if (katan.accountManager.existsAccount(username)) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ACCOUNT_ALREADY_EXISTS
                    )
                    return@post
                }

                val password = (data["password"]!! as String).trim()
                if (username.isBlank() && password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_NOT_VALID
                    )
                    return@post
                }

                val account = katan.accountManager.createAccount(username, password)
                katan.accountManager.registerAccount(account)
                call.respond(HttpStatusCode.Created, KHttpResponse.Ok(account))
            }

            post("/login") {
                val data = call.receive() as Map<*, *>
                if (!data.containsKey("username") || !data.containsKey("password")) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_NOT_SPECIFIED
                    )
                    return@post
                }

                val username = (data["username"]!! as String).trim()
                if ((username.isBlank()) || !katan.accountManager.existsAccount(username)) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_NOT_FOUND
                    )
                    return@post
                }

                val password = (data["password"]!! as String).trim()
                if (password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_NOT_VALID
                    )
                    return@post
                }

                try {
                    val token = katan.accountManager.auth(username, password)
                    call.respond(HttpStatusCode.OK, KHttpResponse.Ok(mapOf("token" to token)))
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_NOT_FOUND
                    )
                } catch (e: IllegalAccessError) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ACCOUNT_WRONG_PASSWORD
                    )
                }
            }

            post("/verify") {
                val data = call.receive() as Map<*, *>
                if (!data.containsKey("token")) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        INVALID_ACCESS_TOKEN
                    )
                    return@post
                }

                try {
                    val account = katan.accountManager.verify(data["token"]!! as String)
                    call.respond(HttpStatusCode.OK, KHttpResponse.Ok(mapOf("account" to account)))
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        INVALID_ACCESS_TOKEN
                    )
                }
            }

            route("/{account}") {
                lateinit var account: KAccount

                intercept(ApplicationCallPipeline.Features) {
                    val accountId = call.parameters["account"]
                    if (accountId == null) {
                        finish()
                        return@intercept
                    }

                    try {
                        account = katan.accountManager.getAccountById(accountId) ?: throw IllegalArgumentException()
                    } catch (e: IllegalArgumentException) {
                        context.respond(
                            HttpStatusCode.BadRequest,
                            ACCOUNT_NOT_FOUND
                        )
                        finish()
                        return@intercept
                    }
                }

                get("/permissions") {
                    context.respond(KHttpResponse.Ok(mapOf("permissions" to KAccount.ALL.map {
                        it.name to account.hasPermission(it.permission)
                    })))
                }
            }
        }

        router.route(Routes.SERVERS_ENDPOINT) {
            get {
                call.respond(KHttpResponse.Ok(katan.serverManager.getServers().map {
                    katan.serverManager.inspectServer(it)
                }))
            }

            post {
                val data = call.receive() as Map<*, *>
                if (!data.containsKey("name") || !data.containsKey("port") || !data.containsKey("owner")) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SERVER_UNSPECIFIED_NOP
                    )
                    return@post
                }

                val name = (data["name"] as? String)?.trim()
                if (name.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SERVER_UNSPECIFIED_NOP
                    )
                    return@post
                }

                try {
                    val server = katan.serverManager.createServer(name, (data["port"]!! as String).toShort())
                    katan.serverManager.addServerHolder(server, data["owner"]!! as String, -1)
                    call.respond(HttpStatusCode.Created, KHttpResponse.Ok(mapOf("server" to server)))
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        SERVER_CONFLICT
                    )
                    return@post
                }

            }

            route("/{server}") {
                lateinit var server: KServer

                intercept(ApplicationCallPipeline.Features) {
                    val serverId = call.parameters["server"]
                    if (serverId == null) {
                        finish()
                        return@intercept
                    }

                    try {
                        server = katan.serverManager.getServer(serverId.toInt())
                    } catch (e: IllegalArgumentException) {
                        context.respond(
                            HttpStatusCode.BadRequest,
                            SERVER_NOT_FOUND
                        )
                        finish()
                        return@intercept
                    }
                }

                get {
                    call.respond(KHttpResponse.Ok(katan.serverManager.inspectServer(server)))
                }

                get("/query") {
                    call.respond(KHttpResponse.Ok(katan.serverManager.queryServer(server)))
                }

                get("/logs") {
                    val logs = mutableListOf<String>()
                    katan.docker.logContainerCmd(server.container.id)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withFollowStream(false)
                        .exec(object : LogContainerResultCallback() {
                            override fun onNext(item: com.github.dockerjava.api.model.Frame) {
                                if (item.streamType == StreamType.RAW) {
                                    for (str in item.payload.toString(StandardCharsets.UTF_8).split('\n')) {
                                        logs.add(str)
                                    }
                                }
                            }
                        }).awaitCompletion(5, TimeUnit.SECONDS)
                    call.respond(KHttpResponse.Ok(logs))
                }

                get("/files") {
                    val execId = katan.docker.execCreateCmd(server.container.id)
                        .withAttachStdin(true)
                        .withTty(true)
                        .withCmd("ls")
                        .exec().id

                    val job = CompletableDeferred<List<Map<String, Any>>>()
                    katan.docker.execStartCmd(execId).exec(object: ResultCallback<com.github.dockerjava.api.model.Frame> {
                            var files = mutableListOf<Map<String, Any>>()
                            var prepend = ""
                            override fun onError(error: Throwable) {}

                            override fun onNext(frame: com.github.dockerjava.api.model.Frame) {
                                val payload = frame.payload.toString(StandardCharsets.UTF_8)
                                files.add(mapOf("value" to payload))
                            }

                            override fun onStart(stream: Closeable?) {}

                            override fun onComplete() {
                                job.complete(files)
                            }

                            override fun close() {}
                        })
                    call.respond(KHttpResponse.Ok(job.await()))
                }

                get("/permissions/{account}") {
                    val accountId = call.parameters["account"]
                    if (server.holders.none { it.account.id.toString() == accountId }) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            SERVER_HOLDER_NOT_FOUND
                        )
                        return@get
                    }

                    val holder = server.holders.first { it.account.id.toString() == accountId }
                    context.respond(KHttpResponse.Ok(mapOf("permissions" to KServerHolder.ALL.map {
                        it.name to holder.hasPermission(it.permission)
                    })))
                }
            }
        }
    }

}