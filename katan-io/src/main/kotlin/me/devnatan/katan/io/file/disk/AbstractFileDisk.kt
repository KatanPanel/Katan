package me.devnatan.katan.io.file.disk

import me.devnatan.katan.api.io.FileDisk

abstract class AbstractFileDisk : FileDisk {

    override val isDirectory: Boolean
        get() = true

    override val alias: String
        get() = name

}