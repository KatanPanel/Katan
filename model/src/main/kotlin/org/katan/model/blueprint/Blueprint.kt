package org.katan.model.blueprint

import kotlinx.datetime.Instant

interface Blueprint {

    val id: Long

    val name: String

    val image: String

    val createdAt: Instant

}
