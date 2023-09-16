package org.katan.model.unit

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.katan.model.Snowflake

typealias KUnit = Unit

@Serializable
class Unit(

    /**
     * The globally unique ID for this unit.
     *
     * This id does not change, and is unique since the creation of this unit, shared with servers
     * even outside the cluster as the synchronization of identifiers is shared between the clusters.
     *
     * This is the internal id of this unit on the network.
     */
    val id: Snowflake,

    /**
     * External id for this unit.
     *
     * The external id of the unit is defined by the user and is used only so that the user can
     * identify it in any way during its communication with the communication channels of Katan.
     *
     * This id can be changed at any time at the user's request and is not restricted in any way.
     */
    val externalId: String?,

    /**
     * Node ID where this unit is located.
     */
    val nodeId: Int,

    /**
     * The name of this unit.
     *
     * The unit name follows a very strict naming pattern since it is shared between communication
     * channels in a similar way to the id, in addition to being used as a second form of
     * identification that can be deferred by the user if necessary, then it is applied restrictions
     * and constraints so that it adapts to these communication channels.
     */
    val name: String,

    /**
     * Display name of this unit.
     *
     * Unlike the regular name, this name can contain special characters and is not escaped or treated
     * in a special way by Katan as it is used only for display character, besides not being unique,
     * that is, there can be more than one unit with the same display name in the same cluster.
     */
//    val displayName: String?

    /**
     * Long description of this unit.
     *
     * The description of the unit can be defined by the user so that it is displayed in Katan's
     * views such as the web user interface or for him to make use of this description by getting
     * the data of that unit by Katan API calls.
     *
     * The value of this property is null if no description has been defined.
     */
//    val description: String?

    /**
     * Local address of this unit.
     *
     * The local address is used within the cluster to communicate with this unit and is not
     * externally accessible.
     */
//    val localAddress: Connection

    /**
     * Remote address of this unit.
     *
     * The remote address is used by external users to communicate with this unit, normally this is
     * the address players use to access this unit.
     */
//    val remoteAddress: Connection

    /**
     * The instant this unit was created.
     */
    val createdAt: Instant,

    /**
     * The last instant this unit was updated.
     */
    val updatedAt: Instant,

    /**
     * The time when this unit was deleted.
     *
     * The value of this property is null if this unit has never been removed.
     */
    val deletedAt: Instant?,

    /**
     * Instance of this unit.
     *
     * The instance of a unit describes internal data of that unit as data in execution, it is
     * separated from the main object of the unit because it is an object of extremely mutable
     * object, that is, its data changes constantly.
     */
    val instanceId: Snowflake?,

    /**
     * Current status of this unit.
     *
     * The drive's status is not its internal status, the internal status is described by the unit's
     * internal information that is accessed through its instance.
     */
    val status: UnitStatus,
)

@Serializable
enum class UnitStatus(val value: String) {
    Unknown("unknown"),
    Created("created"),
    MissingInstance("missing-instance"),
    CreatingInstance("creating-instance"),
    Ready("ready"),
    ;

    companion object {

        fun getByValue(value: String): UnitStatus = entries.firstOrNull {
            it.value.equals(value, ignoreCase = false)
        } ?: Unknown
    }
}
