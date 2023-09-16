package org.katan.model.account

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

public interface Account {

    /**
     * The unique ID of this account.
     */
    public val id: Snowflake

    /**
     * User-provided username of this account.
     */
    public val username: String

    public val email: String

    public val displayName: String?

    /**
     * The instant this account was registered.
     */
    public val createdAt: Instant

    public val updatedAt: Instant

    /**
     * The last instant this account was logged in.
     */
    public val lastLoggedInAt: Instant?

    public val avatar: Snowflake?
}
