package org.katan.model.io

interface Directory : VirtualFile {

    val children: List<VirtualFile>
}
