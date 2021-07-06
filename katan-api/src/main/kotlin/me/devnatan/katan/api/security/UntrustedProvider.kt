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

import me.devnatan.katan.api.Descriptor

/**
 * API allows external entities such as plug-ins and scripts  to provide
 * caching services, encryption, custom permissions to be used in the
 * application lifecycle.
 *
 * We have no control over the identity of these entities, so it is necessary
 * that they have a demarcation so that every time something is treated in
 * the application related to everything that these entities  can do within
 * the life cycle of the application is monitored.
 *
 * @see    me.devnatan.katan.api.security.auth.ExternalAuthenticationProvider
 * @author Natan Vieira
 * @since  1.0
 */
interface UntrustedProvider {

    /**
     * Returns the [Descriptor] that untrusted providers must have in order to
     * be identified and differentiated or equaled to others.
     */
    val descriptor: Descriptor

}