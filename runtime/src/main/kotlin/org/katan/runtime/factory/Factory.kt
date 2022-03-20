package org.katan.runtime.factory

import org.katan.Versioned

interface Factory {

    fun checkSupport(entity: Versioned)

}