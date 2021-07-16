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

package me.devnatan.katan.api.security.role

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.security.permission.PermissionsHolder
import java.time.Instant

/**
 * Roles are entities that carry permissions with a many-to-one relationship,
 * with `one` being the role itself, and `many` being the entity that has a
 * [Role] as a property.
 *
 * The [Role]-entity relationship is an inheritance relationship, as soon as
 * the role has a permission granted and the entity does not have that
 * permission  defined, the entity will inherit the role's permission.
 *
 * Roles are simple objects that contain a [Descriptor] to identify them.
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface Role : PermissionsHolder {

    /**
     * Returns the role id.
     */
    val id: String

    /**
     * Returns the role name.
     */
    var name: String

    /**
     * Returns the [Instant] this role was created.
     */
    val createdAt: Instant

}