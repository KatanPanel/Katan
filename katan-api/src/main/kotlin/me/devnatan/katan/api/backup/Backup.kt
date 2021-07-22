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

package me.devnatan.katan.api.backup

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import java.util.*

interface Backup {

    /**
     * Returns the backup unique id.
     */
    val id: UUID

    /**
     * Returns the backup name.
     */
    val name: String

    /**
     * Returns the time the backup was created or marked to run,
     * or null if it has not been marked to run at any time.
     */
    val scheduledTo: LocalDateTime?

    /**
     * Returns the time the backup was created or marked to run.
     */
    val createdAt: Instant

    /**
     * Returns all the backup run process.
     */
    val workflows: List<BackupWorkflow>

}