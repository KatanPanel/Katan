package org.katan.model.io

public interface Directory : VirtualFile {

    public val children: List<VirtualFile>
}
