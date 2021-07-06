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
import java.time.Instant

/**
 * A single file in a [FileDirectory].
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface File {

    /**
     * Returns the name (including extension) of this file.
     */
    val name: String

    /**
     * Returns the absolute path to this file.
     */
    val path: String

    /**
     * Returns the instant the file was created or `null` if it is not available
     */
    val createdAt: Instant?

    /**
     * Returns the last time the file was modified or `null` if it's not
     * available or the file has never been modified.
     */
    val lastModifiedAt: Instant?

    /**
     * Returns the length of the file.
     */
    val size: Long

    /**
     * Returns if this file is hidden according to the conventions of the underlying [Platform].
     */
    val isHidden: Boolean

}

val File.extension
    inline get() = name.substringAfterLast(".")