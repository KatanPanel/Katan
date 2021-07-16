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

package me.devnatan.katan.api.security.account

import me.devnatan.katan.api.security.Credentials
import me.devnatan.katan.api.security.permission.PermissionsHolder
import me.devnatan.katan.api.security.role.Role
import java.time.Instant
import java.util.*

/**
 * Represents an account, initially only on the Katan Web Server and later on the CLI as well.
 * Accounts can be used when authentication is required to perform something.
 */
interface Account : PermissionsHolder {

    /**
     * Returns the unique account identification.
     */
    val id: UUID

    /**
     * Returns the account username.
     */
    val username: String

    /**
     * Returns when this account was registered.
     */
    val registeredAt: Instant

    /**
     * Returns the last time you authenticated with this account.
     */
    var lastLogin: Instant?

    /**
     * Returns the current [Role] of the account or `null` if it has no role.
     */
    var role: Role?

    var credentials: Credentials

}