package org.katan.config

import com.sksamuel.hoplite.ConfigLoaderBuilder

internal object DefaultConfigLoader : ConfigLoader {

    override fun load(): KatanConfig {
        return ConfigLoaderBuilder.default()
            .strict()
            .build()
            .loadConfigOrThrow<ConfigImpl>(
                "/application.conf"
            )
    }
}
