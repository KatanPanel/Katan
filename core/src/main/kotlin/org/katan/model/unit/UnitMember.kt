package org.katan.model.unit

import org.katan.model.PermissionsHolder
import org.katan.model.Snowflake

public interface UnitMember : PermissionsHolder {

    public val accountId: Snowflake
}
