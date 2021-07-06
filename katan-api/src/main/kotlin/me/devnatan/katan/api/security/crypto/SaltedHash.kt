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

package me.devnatan.katan.api.security.crypto

/**
 * Represents an salted hashing algorithm.
 *
 * Unlike [Hash], this interface indicates that the hashing method
 * to be used will use a salt of a specific length ([saltLength]).
 *
 * By using salt, hash methods from this interface are generally safer.
 */
interface SaltedHash : Hash {

    /**
     * Returns the length of the salt that will be used in the hashing process for that algorithm.
     */
    val saltLength: Int

}