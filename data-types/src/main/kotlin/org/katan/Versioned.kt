package org.katan

typealias EntityVersion = Int

interface Versioned {

    val entityVersion: EntityVersion

}