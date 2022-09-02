package org.katan.service.fs.impl

import org.katan.model.fs.Directory
import org.katan.model.fs.VirtualFile

public data class DirectoryImpl(
    private val file: VirtualFile,
    override val children: List<VirtualFile>
) : Directory, VirtualFile by file
