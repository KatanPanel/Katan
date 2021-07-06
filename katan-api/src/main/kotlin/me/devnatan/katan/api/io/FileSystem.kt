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

import me.devnatan.katan.api.Platform

/**
 * A file system capable of storing, manage, creating, reading, writing and
 * listing file disks.
 *
 * Taking into account that the work environment can be any, it is unknown,
 * the operations in a [FileSystem] must take has a property called [platform]
 * to treat them in a way that they adapt and can make the transition from what
 * was given a solution on your platform.
 *
 * File system implementations are thread-safe and operations are
 * cancellable by default.
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface FileSystem {

    /**
     * The actual platform of this file system
     */
    val platform: Platform

    /**
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend fun listDisks(): List<FileDisk>

    /**
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend fun getDisk(name: String): FileDisk?

    /**
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend fun existsDisk(name: String): Boolean

    /**
     * Returns a [Iterator] for this file system.
     * @throws UnauthorizedFileSystemAccessException
     */
    suspend operator fun iterator(): Iterator<FileDisk>

}