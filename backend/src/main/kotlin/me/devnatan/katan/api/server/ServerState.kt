package me.devnatan.katan.api.server

enum class ServerState {

    STOPPED,

    STARTING,

    RUNNING;

    val isRunning: Boolean
        get() = this != STOPPED

}