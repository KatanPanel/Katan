package me.devnatan.katan.api.cli

import me.devnatan.katan.api.plugin.Plugin

class RegisteredCommand(val plugin: Plugin, command: Command): Command by command