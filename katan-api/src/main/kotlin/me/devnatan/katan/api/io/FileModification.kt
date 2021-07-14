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

import java.time.Instant

/**
 * Represents a modification made to a [File].
 */
interface FileModification {

    /**
     * Returns the subject of the change.
     */
    val mutator: Any

    /**
     * Returns the [Instant] when the modification occurred.
     */
    val modifiedAt: Instant

    /**
     * Returns the reason why the file was modified.
     */
    val cause: Cause

    /**
     * Represents the cause of the modification of a file.
     */
    sealed class Cause(val type: String) {

        /**
         * The cause of the file being modified is that it has been renamed.
         */
        object Write : Cause("write")

        /**
         * The cause of the file being modified is that it has been renamed.
         */
        class Rename(val oldName: String) : Cause("rename")

    }

}