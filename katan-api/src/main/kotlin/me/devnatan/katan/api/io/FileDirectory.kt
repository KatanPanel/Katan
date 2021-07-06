/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.devnatan.katan.api.io

/**
 * A file directory is capable of storing a collection of [File], containing
 * all the properties of a regular [File].
 *
 * Instances of this class currently do not represent a file system, it resides
 * in a [FileDisk] and read, write and access operations must respect the
 * permissions of its respective [FileDisk].
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface FileDirectory : File {

    /**
     * Returns all files in that directory.
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend fun listFiles(): List<File>

    /**
     * Returns a [File] contained in that directory that has the specified
     * name or `null` if the file does not exist.
     * @param name the name of the file (including the extension)
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend fun getFile(name: String): File?

    /**
     * Returns a [Iterator] for this directory.
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend operator fun iterator(): Iterator<File>

}

/**
 * Returns all files in that directory that match a specific extension.
 * @param extension the file extension
 * @since 1.0
 */
suspend inline fun FileDirectory.listFilesByExtension(
    extension: CharSequence
): List<File> {
    return listFiles().filter { it.extension == extension }
}