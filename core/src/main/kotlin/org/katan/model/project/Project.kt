package org.katan.model.project

import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

@Serializable
data class Project(
    val id: Snowflake,
    val name: String,
)
