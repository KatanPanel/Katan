package org.katan.crypto

import org.koin.core.module.Module
import org.koin.dsl.module

public val cryptoDI: Module = module {
    single<Hash> { SHA1Hash() }
    single<SaltedHash> { BcryptHash() }
}
