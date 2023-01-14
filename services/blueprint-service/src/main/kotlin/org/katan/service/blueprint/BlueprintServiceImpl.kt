package org.katan.service.blueprint

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.io.FileNotAccessibleException
import org.katan.model.io.VirtualFile
import org.katan.service.blueprint.model.BlueprintImpl
import org.katan.service.blueprint.model.ImportedBlueprint
import org.katan.service.blueprint.model.RawBlueprintImpl
import org.katan.service.blueprint.provider.BlueprintResourceProvider
import org.katan.service.blueprint.repository.BlueprintEntity
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.fs.FSService
import org.katan.service.id.IdService
import java.io.File

internal class BlueprintServiceImpl(
    private val idService: IdService,
    private val blueprintRepository: BlueprintRepository,
    private val blueprintResourceProvider: BlueprintResourceProvider,
    private val fsService: FSService
) : BlueprintService {

    companion object {
        // TODO use non-hardcoded blueprints fs dir & main file name
        private const val FS_DIR = "blueprints"
        private const val MAIN = "main.conf"
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val hocon = Hocon {
        useConfigNamingConvention = true
    }

    override suspend fun listBlueprints(): List<Blueprint> {
        return blueprintRepository.findAll().map { it.toModel() }
    }

    override suspend fun getRaw(id: Long): RawBlueprint {
        TODO("get raw from local files :)")
    }

    override suspend fun getBlueprint(id: Long): Blueprint {
        return blueprintRepository.find(id)?.toModel() ?: throw BlueprintNotFoundException()
    }

    override suspend fun importBlueprint(url: String): ImportedBlueprint {
        val provided = blueprintResourceProvider.provideFrom(url)
            ?: throw BlueprintNotFoundException()

        val raw = provided.main.raw

        val existing = findExistingFromRaw(raw)
        if (existing != null) {
            throw BlueprintConflictException()
        }

        val id = idService.generate()
        writeFileToLocal(id, MAIN, provided.main.contents)

        for (asset in provided.assets)
            writeFileToLocal(id, asset.name, asset.contents)

        registerBlueprint(id, raw)
        return ImportedBlueprint(id, provided)
    }

    override suspend fun readBlueprintAssetContents(
        id: Long,
        path: String
    ): Pair<VirtualFile, ByteArray> {
        if (path.equals(MAIN, ignoreCase = true)) {
            throw FileNotAccessibleException()
        }

        return readFileFromLocal(id, path)
    }

    private suspend fun writeFileToLocal(id: Long, name: String, contents: ByteArray) {
        fsService.uploadFile(null, rootDirectoryFor(id), name, contents)
    }

    private suspend fun readFileFromLocal(id: Long, name: String): Pair<VirtualFile, ByteArray> {
        return fsService.readFile(null, rootDirectoryFor(id), name)
    }

    private suspend fun readMainFile(id: Long): RawBlueprint {
        val (_, bytes) = readFileFromLocal(id, MAIN)
        val config = ConfigFactory.parseString(
            bytes.decodeToString(),
            ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
        )

        @OptIn(ExperimentalSerializationApi::class)
        return hocon.decodeFromConfig<RawBlueprintImpl>(config)
    }

    private fun rootDirectoryFor(id: Long): String {
        return FS_DIR + File.separator + id
    }

    private suspend fun registerBlueprint(id: Long, raw: RawBlueprint) {
        blueprintRepository.create(
            BlueprintImpl(
                id = id,
                name = raw.name,
                version = raw.version,
                imageId = raw.build.image,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )
    }

    private fun findExistingFromRaw(rawBlueprint: RawBlueprint): Blueprint? {
        // TODO
        return null
    }

    private fun BlueprintEntity.toModel(): Blueprint {
        return BlueprintImpl(
            id = getId(),
            name = name,
            version = version,
            imageId = imageId,
            createdAt = createdAt,
            updatedAt = updatedAt ?: createdAt
        )
    }
}
