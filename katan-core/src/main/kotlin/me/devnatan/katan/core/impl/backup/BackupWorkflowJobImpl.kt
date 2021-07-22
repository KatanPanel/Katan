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
import me.devnatan.katan.api.backup.BackupWorkflowJob

@Serializable
data class BackupWorkflowJobImpl(
    override val id: String,
    override val startedAt: Instant
) : BackupWorkflowJob {

    override var status: BackupWorkflowJob.Status = BackupWorkflowJob.Status.NONE
    override var finishedAt: Instant = Instant.DISTANT_PAST
        get() = if (status == BackupWorkflowJob.Status.NONE)
            error("Job is not active")
        else field

}