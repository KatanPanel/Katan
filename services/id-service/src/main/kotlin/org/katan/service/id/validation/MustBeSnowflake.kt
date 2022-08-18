package org.katan.service.id.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

private const val TEMPLATE = "Must be a valid snowflake id"

@Target(FIELD, TYPE, ANNOTATION_CLASS)
@MustBeDocumented
@Retention(RUNTIME)
@Constraint(validatedBy = [MustBeSnowflakeValidator::class])
public annotation class MustBeSnowflake(
    val message: String = TEMPLATE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

public class MustBeSnowflakeValidator : ConstraintValidator<MustBeSnowflake, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        val isLongValue = value?.toLongOrNull() != null
        if (!isLongValue) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                TEMPLATE
            ).addConstraintViolation()
        }

        return isLongValue
    }
}
