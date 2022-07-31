package org.katan.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

internal object DefaultConfigLoader : ConfigLoader {

    override fun load(): KatanConfig {
        return ConfigLoaderBuilder.default()
            .addResourceSource("/application-prod.conf", optional = true)
            .addResourceSource("/application-dev.conf", optional = true)
            .strict()
            .build()
            .loadConfigOrThrow<ConfigImpl>("/application.conf")
    }
}
