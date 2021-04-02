package me.devnatan.katan.api.security.permission

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.security.permission.PermissionTarget.ACCOUNT
import me.devnatan.katan.api.security.permission.PermissionTarget.ROLE
import me.devnatan.katan.api.security.permission.PermissionTarget.SERVER_HOLDER

/**
 * Permission keys are used to identify permissions on [PermissionsHolder]
 * entities. These keys can be defined by [Untrus] who have a
 * [Descriptor].
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface PermissionKey {

    val id: String

    val overwrite: Array<out PermissionKey>

    val targets: Int

    val owner: Descriptor?

    companion object {

        val defaultPermissionKeys: Array<out PermissionKey> by lazy {
            arrayOf(
                ViewServerConsole,
                UseServerConsole,
                ViewServerFS,
                AccessServerFS,
                ViewServerFSDisk,
                AccessServerFSDisk,
                ViewServerFSDisksFiles,
                EditServerFSDisksFiles
            )
        }

    }

}

internal class PermissionKeyImpl(
    override val id: String,
    override val targets: Int = ACCOUNT or ROLE or SERVER_HOLDER,
    override val overwrite: Array<out PermissionKey> = emptyArray(),
    override val owner: Descriptor? = null
) : PermissionKey

fun PermissionKey(
    id: String,
    overwrite: Array<out PermissionKey>,
    target: Int,
    owner: Descriptor
): PermissionKey {
    return PermissionKeyImpl(id, target, overwrite, owner)
}