package org.katan.service.fs

import org.katan.model.fs.Bucket
import org.katan.model.fs.VirtualFile

public interface FSService {

    public suspend fun getBucket(path: String): Bucket?

    public suspend fun getFile(path: String): VirtualFile?

}