package me.devnatan.katan.frontend

import kotlinx.html.dom.append
import kotlinx.html.js.code
import kotlinx.html.js.p
import kotlinx.html.js.pre
import org.w3c.dom.WebSocket
import kotlin.browser.document

fun main() {
    val out = document.querySelector(".console .output")!!
    out.append {
        pre {
            for (i in 0..30) {
                p { code { +"Teste $i" } }
            }
        }
    }
    connectWebsocket()
}

@JsName("connectWebsocket")
private fun connectWebsocket() {
    val ws = WebSocket("ws://127.0.0.1:8080")
    ws.onopen = {
        ws.send("test")
    }

    ws.onmessage = {
        console.log(it.data)
    }

    ws.onclose = {
        console.log("socket close")
    }

    ws.onerror = {
        console.log("socket error")
    }
}