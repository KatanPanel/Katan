package org.katan.runtime

import org.katan.EntityVersion
import org.katan.Versioned
import org.katan.runtime.factory.Factory

class FactoryVersioniningTests {

    fun `throw UnsupportedEntityException`() {
        val factory = object: Factory {
            override val minimumSupportedVersion: EntityVersion
                get() = TODO("Not yet implemented")
            override fun checkSupport(entity: Versioned) {

            }
        }
    }

}