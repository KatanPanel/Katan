package org.katan.model.fs

interface Directory : VirtualFile {

    val children: List<VirtualFile>

}