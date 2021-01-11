package me.devnatan.katan.api.io

interface FileDisk : File {

    val id: String

    val kind: String

    val alias: String

    suspend fun listFiles(): List<File>

}

suspend inline fun FileDisk.listFilesByExtension(extension: CharSequence): List<File> {
    return listFiles().filter { it.name.substringAfterLast(".") == extension }
}