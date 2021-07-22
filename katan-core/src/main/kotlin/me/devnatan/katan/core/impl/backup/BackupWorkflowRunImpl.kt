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

package me.devnatan.katan.core.impl.backup

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import me.devnatan.katan.api.backup.BackupTriggerable
import me.devnatan.katan.api.backup.BackupWorkflowJob
import me.devnatan.katan.api.backup.BackupWorkflowRun

@Serializable
data class BackupWorkflowRunImpl(
    override val id: Long,
    override val author: BackupTriggerable?,
    override val startedAt: Instant,
) : BackupWorkflowRun {

    override var isActive: Boolean = false
    override var isSuccessful: Boolean = false

    private var _finishedAt: Instant? = null

    override val finishedAt: Instant
        get() = if (isActive)
            error("Not finished yet, use `isActive()` before")
        else _finishedAt!!

    override val jobs: MutableList<BackupWorkflowJob> = mutableListOf()

}