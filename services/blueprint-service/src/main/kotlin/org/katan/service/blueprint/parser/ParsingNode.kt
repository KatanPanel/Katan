package org.katan.service.blueprint.parser

import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal data class ParsingNode<T>(
    internal val property: SpecProperty<T>,
    internal val config: ConfigValue?
) : ReadOnlyProperty<Any?, T> {

    private val validators = mutableListOf<ParsingNodeValidator>()

    @Suppress("UNCHECKED_CAST")
    internal val value: T get() = config?.unwrapped() as T

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        validate()
        return value
    }

    private fun validate() {
        for (validator in validators) {
            validator.validate(this)
        }
    }

    internal fun withValidator(validator: ParsingNodeValidator) = apply {
        validators.add(validator)
    }
}

internal interface ParsingNodeValidator {
    fun <T> validate(node: ParsingNode<T>): ParsingNode<*>
}

internal typealias ParsingNodeValidatorMessage = () -> String

internal class RequiredPropertyValidator(private val message: ParsingNodeValidatorMessage?) :
    ParsingNodeValidator {

    override fun <T> validate(node: ParsingNode<T>): ParsingNode<*> {
        if (node.config == null) throw RequiredPropertyException(node.property, message?.invoke())
        return node
    }
}

internal class NotBlankValidator(private val message: ParsingNodeValidatorMessage?) :
    ParsingNodeValidator {

    override fun <T> validate(node: ParsingNode<T>): ParsingNode<*> {
        if ((node.value as? String)?.isBlank() == true) {
            throw BlankPropertyException(node.property, message?.invoke())
        }

        return node
    }
}

internal fun <T> ConfigValue.parsing(property: SpecProperty<T>): ParsingNode<T> {
    return ParsingNode(
        property,
        when (valueType()) {
            ConfigValueType.OBJECT -> (this as ConfigObject)[property.name]
            else -> error("Only objects can reference a node")
        }
    )
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> ParsingNode<T>.required(noinline message: ParsingNodeValidatorMessage? = null) =
    withValidator(RequiredPropertyValidator(message))

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> ParsingNode<T>.notBlank(noinline message: ParsingNodeValidatorMessage? = null) =
    withValidator(NotBlankValidator(message))
