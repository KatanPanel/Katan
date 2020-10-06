package me.devnatan.katan.api.plugin

sealed class PluginState {

    object Loaded

    object Enabled

    object Disabled

}