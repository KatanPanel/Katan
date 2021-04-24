package me.devnatan.katan.api.composition

import java.time.Instant

interface CompositionStore<out T : CompositionOptions> {

    val key: Composition.Key

    val options: T

    val lastModifiedAt: Instant?

    operator fun component1() = options

}