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