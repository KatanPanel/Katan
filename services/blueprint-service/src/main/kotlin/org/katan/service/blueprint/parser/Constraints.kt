package org.katan.service.blueprint.parser

import kotlin.reflect.KClass

internal fun interface PropertyConstraint {

    fun check(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?)
}

internal object RequiredPropertyConstraint : PropertyConstraint {
    override fun check(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?) {
        checkNotNull(actualKind) { "${property.qualifiedName} is required" }
    }
}

internal object NotBlankPropertyConstraint : PropertyConstraint {
    override fun check(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?) {
        if (value !is String) {
            // ignore mixed kinds because constraints are applied on property-level and here we
            // can have a non-literal node unwrapped value being validated
            if (property.kind is PropertyKind.Mixed) return

            error("Cannot check emptiness for non-literal value at ${property.qualifiedName}")
        }

        check(value.isNotBlank()) { "${property.qualifiedName} cannot be blank" }
    }
}

internal object EnvironmentVariableConstraint : PropertyConstraint {
    override fun check(property: Property, actualKind: KClass<out PropertyKind>?, value: Any?) {
        // TODO
    }
}
