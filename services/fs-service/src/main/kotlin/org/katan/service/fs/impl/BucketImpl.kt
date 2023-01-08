package org.katan.service.fs.impl

import kotlinx.datetime.Instant
import org.katan.model.io.Bucket

public data class BucketImpl(
    override val path: String,
    override val name: String,
    override val isLocal: Boolean,
    override val createdAt: Instant?
) : Bucket
