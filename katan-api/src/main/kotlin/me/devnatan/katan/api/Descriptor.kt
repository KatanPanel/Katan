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

package me.devnatan.katan.api

import java.io.Serializable

/**
 * Descriptors are fundamental objects for the security and reliability of the
 * application, and are used in several areas and in different ways.
 *
 * Every entity within the application must have a descriptor identifying it
 * and revealing whether it is reliable or not.
 *
 * Entities with untrusted descriptors may not have permissions to access
 * certain areas of the application resulting in a [SecurityException].
 *
 * Implementation note:
 *   - The rule for checking a descriptor's equality is transitive, that if
 * one descriptor has the same id as the other they are equal regardless
 * of the rest of the properties. Returns `true` if this descriptor is equal
 * to other.
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface Descriptor : Serializable {

    /**
     * Returns the entity id of this descriptor.
     */
    val id: String

    /**
     * Returns `true` if the entity holding this descriptor is a marked as
     * trustworthy entity or `false` otherwise.
     */
    fun isTrusted(): Boolean

}