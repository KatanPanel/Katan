package me.devnatan.katan.api.security

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
 * Security credentials must not contain mutable or public properties.
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
    fun validate(value: CharSequence): Boolean

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
inline class BasicCredentials(private val value: CharSequence) :
    Credentials {

    override fun validate(value: CharSequence): Boolean {
        return this.value == value
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