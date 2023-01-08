package org.katan.model.unit

import org.katan.model.PermissionsHolder
import org.katan.model.Snowflake

interface UnitMember : PermissionsHolder {

    val accountId: Snowflake
}
