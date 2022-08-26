package org.katan.service.fs

import org.katan.model.fs.Bucket
import org.katan.model.fs.VirtualFile

public interface FSService {

    public suspend fun getFile(bucket: String, destination: String, path: String): VirtualFile?

    public suspend fun getBucket(bucket: String, destination: String): Bucket?
}
