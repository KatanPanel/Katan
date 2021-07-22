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

package me.devnatan.katan.api.event

import me.devnatan.katan.api.backup.Backup
import me.devnatan.katan.api.backup.BackupWorkflow
import java.time.Instant

/**
 * Backup events are launched when new backups are created, deleted, run or when
 * your workflow is run, you can use events that listen individually to [BackupWorkflow].
 *
 * **NOTE: [BackupWorkflow] events are cancelable but cannot be modified.**
 *
 * @see Event
 * @see RemoteEvent
 * @author Natan Vieira
 */
interface BackupEvent : RemoteEvent {

    /**
     * Returns the [Backup] of this event.
     */
    val backup: Backup

}

/**
 * Called when a new [Backup] is created.
 *
 * @property backup the backup
 * @property source the event origin
 * @property createdAt when the event happened
 * @see BackupEvent
 * @author Natan Vieira
 */
data class BackupCreatedEvent(
    override val backup: Backup,
    override val source: EventSource,
    val createdAt: Instant
) : BackupEvent

/**
 * Called when a [Backup] is deleted.
 *
 * @property backup the backup
 * @property source the event origin
 * @property deletedAt when the event happened
 * @see BackupEvent
 * @author Natan Vieira
 */
data class BackupDeletedEvent(
    override val backup: Backup,
    override val source: EventSource,
    val deletedAt: Instant
) : BackupEvent

/**
 * Called when a [Backup] is scheduled to execute.
 * To get when it was scheduled to happen use [Backup.scheduledTo].
 *
 * @property source the event origin
 * @property backup the backup
 * @see BackupEvent
 * @author Natan Vieira
 */
data class BackupScheduledEvent(
    override val backup: Backup,
    override val source: EventSource
) : BackupEvent

/**
 * Called when a [Backup] is renamed.
 *
 * @property source the event origin
 * @property backup the backup
 * @property name the new backup name
 * @see BackupEvent
 * @author Natan Vieira
 */
data class BackupRenamedEvent(
    override val backup: Backup,
    override val source: EventSource,
    val name: String
) : BackupEvent

/**
 * [BackupWorkflow] related events.
 *
 * @see BackupEvent
 * @see BackupWorkflow
 * @author Natan Vieira
 */
interface BackupWorkflowEvent : BackupEvent {

    /**
     * Returns the workflow related to this event.
     */
    val workflow: BackupWorkflow

}

/**
 * Called when a [BackupWorkflow] is created.
 *
 * @property backup the backup
 * @property source the event origin
 * @property workflow the backup workflow
 * @see BackupWorkflowEvent
 * @author Natan Vieira
 */
data class BackupWorkflowCreatedEvent(
    override val backup: Backup,
    override val source: EventSource,
    override val workflow: BackupWorkflow
) : BackupWorkflowEvent