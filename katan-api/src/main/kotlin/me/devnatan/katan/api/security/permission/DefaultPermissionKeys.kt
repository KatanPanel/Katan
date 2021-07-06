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

package me.devnatan.katan.api.security.permission

/**
 * View the server console.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val ViewServerConsole: PermissionKey = PermissionKeyImpl(
    "view-server-console", PermissionTarget.SERVER_HOLDER
)

/**
 * Run commands at the server console.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val UseServerConsole: PermissionKey = PermissionKeyImpl(
    "use-server-console", PermissionTarget.SERVER_HOLDER,
    arrayOf(ViewServerConsole)
)

/**
 * View the server's file manager.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val ViewServerFS: PermissionKey = PermissionKeyImpl(
    "view-server-fs", PermissionTarget.SERVER_HOLDER
)

/**
 * Access the server's file manager.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val AccessServerFS: PermissionKey = PermissionKeyImpl(
    "access-server-fs", PermissionTarget.SERVER_HOLDER,
    arrayOf(ViewServerFS)
)

/**
 * View information on a server's file system disks.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val ViewServerFSDisk: PermissionKey = PermissionKeyImpl(
    "view-server-fs-disks", PermissionTarget.SERVER_HOLDER
)

/**
 * Access disks from a server's file system.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val AccessServerFSDisk: PermissionKey = PermissionKeyImpl(
    "view-server-fs-disks", PermissionTarget.SERVER_HOLDER,
    arrayOf(ViewServerFSDisk)
)

/**
 * View the list of files on a disk in a server's file system.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val ViewServerFSDisksFiles: PermissionKey = PermissionKeyImpl(
    "view-server-fs-disks-files", PermissionTarget.SERVER_HOLDER
)

/**
 * Edit files on a disk in a server's file system.
 *
 * @author Natan Vieira
 * @since  1.0
 */
val EditServerFSDisksFiles: PermissionKey = PermissionKeyImpl(
    "edit-server-fs-disks-files", PermissionTarget.SERVER_HOLDER,
    arrayOf(ViewServerFSDisksFiles)
)