package org.katan.model.io

import kotlinx.datetime.Instant

public interface Bucket {

    public val path: String

    public val name: String

    public val isLocal: Boolean

    public val createdAt: Instant?
}
