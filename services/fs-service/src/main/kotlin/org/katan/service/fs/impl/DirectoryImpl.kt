package org.katan.service.fs.impl

import org.katan.model.io.Directory
import org.katan.model.io.VirtualFile

public data class DirectoryImpl(
    private val file: VirtualFile,
    override val children: List<VirtualFile>
) : Directory, VirtualFile by file
