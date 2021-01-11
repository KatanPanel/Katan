package me.devnatan.katan.fs.disk

import me.devnatan.katan.api.io.FileDisk
import me.devnatan.katan.api.io.FileModification

abstract class AbstractFileDisk : FileDisk {

    override val lastModification: FileModification? = null

    override val isDirectory: Boolean
        get() = true

    override val alias: String
        get() = name

}