package me.devnatan.katan.api.security

/**
 * Represents providers of authentication, caching, hashing and other things
 * that Katan cannot determine identity, nor its level of reliability.
 *
 * Implementations of this interface will be automatically checked and
 * will go through several conditions and filters to enter the ecosystem.
 */
interface UntrustedProvider