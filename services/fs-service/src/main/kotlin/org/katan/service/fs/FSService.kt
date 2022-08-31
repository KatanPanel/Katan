package org.katan.service.fs

import org.katan.model.fs.Bucket
import org.katan.model.fs.VirtualFile
import java.io.File

public interface FSService {

    public suspend fun getFile(bucket: String, destination: String, path: String): VirtualFile?

    public suspend fun readFile(path: String, startIndex: Int?, endIndex: Int?): File

    public suspend fun getBucket(bucket: String, destination: String): Bucket?
}
