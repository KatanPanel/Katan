package org.katan.service.fs

import org.katan.model.io.Bucket
import org.katan.model.io.FileLike
import java.io.File

interface FSService {

    suspend fun getFile(bucket: String?, destination: String, path: String): FileLike?

    suspend fun readFile(path: String, startIndex: Int?, endIndex: Int?): File

    suspend fun readFile(bucket: String?, destination: String, name: String): ByteArray

    suspend fun getBucket(bucket: String, destination: String): Bucket?

    suspend fun uploadFile(bucket: String?, destination: String, name: String, contents: ByteArray): FileLike
}
