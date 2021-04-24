package me.devnatan.katan.core.impl.server.docker

import com.github.dockerjava.api.command.InspectContainerResponse
import me.devnatan.katan.api.server.ServerContainerInspection

class DockerServerContainerInspection(val response: InspectContainerResponse) : ServerContainerInspection