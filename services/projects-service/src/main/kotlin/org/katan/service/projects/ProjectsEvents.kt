package org.katan.service.projects

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.katan.model.project.Project

@Serializable
public data class ProjectCreatedEvent(public val project: @Contextual Project)