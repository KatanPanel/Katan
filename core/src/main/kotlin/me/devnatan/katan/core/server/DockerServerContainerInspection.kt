package me.devnatan.katan.core.server

import com.github.dockerjava.api.command.InspectContainerResponse
import me.devnatan.katan.api.server.ServerInspection

class DockerServerContainerInspection(val response: InspectContainerResponse) : ServerInspection