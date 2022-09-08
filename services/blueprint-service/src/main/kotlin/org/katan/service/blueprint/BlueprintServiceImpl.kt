package org.katan.service.blueprint

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import io.ktor.client.HttpClient
import kotlinx.datetime.Clock
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.model.blueprint.Blueprint
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.fs.FileNotAccessibleException
import org.katan.model.fs.VirtualFile
import org.katan.service.blueprint.model.BlueprintImpl
import org.katan.service.blueprint.model.ImportedBlueprint
import org.katan.service.blueprint.model.RawBlueprintImpl
import org.katan.service.blueprint.provider.BlueprintResourceProviderRegistry
import org.katan.service.blueprint.provider.GithubBlueprintResourceProvider
import org.katan.service.blueprint.repository.BlueprintEntity
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.fs.FSService
import org.katan.service.id.IdService
import org.katan.service.instance.InstanceService
import java.io.File

internal class BlueprintServiceImpl(
    private val idService: IdService,
    private val instanceService: InstanceService,
    private val httpClient: HttpClient,
    private val repository: BlueprintRepository,
    private val providerRegistry: BlueprintResourceProviderRegistry,
    private val fsService: FSService
) : BlueprintService {

    companion object {
        private val logger: Logger = LogManager.getLogger(BlueprintServiceImpl::class.java)

        private const val FS_DIR = "blueprints"
        private const val MAIN = "main.conf"
    }

    private val hocon = Hocon {
        useConfigNamingConvention = true
    }

    init {
        providerRegistry.register(GithubBlueprintResourceProvider(httpClient))
    }

    override suspend fun listBlueprints(): List<Blueprint> {
        return repository.findAll().map { it.toModel() }
    }

    override suspend fun getBlueprint(id: Long): Blueprint {
        return repository.find(id)?.toModel() ?: throw BlueprintNotFoundException()
    }

    override suspend fun importBlueprint(url: String): ImportedBlueprint {
        logger.debug("Importing blueprint from \"$url\"...")
        val provider = providerRegistry.findAnyProvider(url)
            ?: throw NoMatchingProviderException()

        logger.debug("Found provider: ${provider.id}")
        val provided = provider.provideFrom(url) ?: throw BlueprintNotFoundException()

        val raw = provided.main.raw
        logger.debug("Importing raw ${raw.name}...")

        val existing = findExistingFromRaw(raw)
        if (existing != null)
            throw BlueprintConflictException()

        val id = idService.generate()
        logger.debug("Saving blueprint locally...")
        writeFileToLocal(id, MAIN, provided.main.contents)

        for (asset in provided.assets)
            writeFileToLocal(id, asset.name, asset.contents)

        logger.debug("Registering blueprint locally...")
        registerBlueprint(id, raw)
//
//        logger.debug("Blueprint registered as $id. Creating instance...")
//        instanceService.createInstance(
//            image = raw.build.image,
//            blueprint = raw,
//            host = null,
//            port = null
//        )

        return ImportedBlueprint(id, provided)
    }

    override suspend fun readBlueprintAssetContents(
        id: Long,
        path: String
    ): Pair<VirtualFile, ByteArray> {
        if (path.equals(MAIN, ignoreCase = true))
            throw FileNotAccessibleException()

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

        return hocon.decodeFromConfig<RawBlueprintImpl>(config)
    }

    private fun rootDirectoryFor(id: Long): String {
        return FS_DIR + File.separator + id
    }

    private suspend fun registerBlueprint(id: Long, raw: RawBlueprint) {
        repository.create(
            BlueprintImpl(
                id = id,
                name = raw.name,
                version = raw.version,
                imageId = raw.build.image,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
                raw = raw
            )
        )
    }

    private suspend fun findExistingFromRaw(rawBlueprint: RawBlueprint): Blueprint? {
        return null
    }

    private suspend fun BlueprintEntity.toModel(): Blueprint {
        return BlueprintImpl(
            id = getId(),
            name = name,
            version = version,
            imageId = imageId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            raw = readMainFile(getId())
        )
    }

}
