package org.katan.crypto.di

import org.katan.crypto.BcryptHash
import org.katan.crypto.Hash
import org.katan.crypto.SHA1Hash
import org.katan.crypto.SaltedHash
import org.koin.core.module.Module
import org.koin.dsl.module

val cryptoDI: Module = module {
    single<Hash> { SHA1Hash() }
    single<SaltedHash> { BcryptHash() }
}