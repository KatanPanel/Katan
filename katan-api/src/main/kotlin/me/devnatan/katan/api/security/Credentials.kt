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

package me.devnatan.katan.api.security

import me.devnatan.katan.api.security.crypto.Hash
import java.io.Serializable

/**
 * Security credentials are used to validate forms, they
 * are a way to protect an entity, data or property.
 *
 * Credentials keep the value of the credential to be verified later, so
 * there is no need for an entity-validator, validation is done at the time of
 * validation. Any type of entity must not be linked to the implementation of
 * the credential.
 *
 * @implNote
 * Security credentials must not contain mutable properties.
 *
 * @author Natan Vieira
 * @see    BasicCredentials
 * @since  1.0
 */
interface Credentials : Serializable {

    /**
     * Performs the verification of the credentials validation from the [value].
     * Returns `true` if validation was performed successfully or `false`
     * otherwise.
     *
     * @throws InvalidCredentialsException if validation was not possible for
     * some reason.
     */
    fun validate(value: String): Boolean

}

/**
 * @author Natan Vieira
 * @see    PasswordCredentials
 * @since  1.0
 */
interface HashedCredentials : Credentials {

    override fun validate(value: String): Boolean {
        throw UnsupportedOperationException(
            "Use validate(value, hash) instead."
        )
    }

    /**
     * Performs the verification of the credentials validation from the [value].
     * Returns `true` if validation was performed successfully or `false`
     * otherwise.
     *
     * @throws InvalidCredentialsException if validation was not possible for
     * some reason.
     */
    fun validate(value: String, hash: Hash): Boolean

}

/**
 * Basic implementation of a credential with validation by [equals] only.
 *
 * @property value the value stored in the credential.
 *
 * @author Natan Vieira
 * @see    Credentials
 * @since  1.0
 */
inline class BasicCredentials(val value: String) :
    Credentials {

    override fun validate(value: String): Boolean {
        return this.value.contentEquals(value)
    }

}

inline class PasswordCredentials(private val nonHashedValue: CharArray) :
    HashedCredentials {

    override fun validate(value: String, hash: Hash): Boolean {
        // check this because hashing algorithms
        // are strict with data length.
        if (nonHashedValue.isEmpty() && value.isEmpty())
            return true

        return hash.compare(nonHashedValue, value)
    }

}

inline class HashedPasswordCredentials(val hashedValue: String) :
    HashedCredentials {

    override fun validate(value: String, hash: Hash): Boolean {
        TODO()
    }

}

object EmptyCredentials : Credentials {

    override fun validate(value: String): Boolean {
        return true
    }

}

/**
 * Thrown if there is a problem during the validation of a [Credentials].
 *
 * @author Natan Vieira
 * @see    Credentials.validate
 * @since  1.0
 */
class InvalidCredentialsException(
    message: String? = null,
    cause: Throwable? = null
) : SecurityException(message, cause)

fun Credentials.isEmpty() = this is EmptyCredentials