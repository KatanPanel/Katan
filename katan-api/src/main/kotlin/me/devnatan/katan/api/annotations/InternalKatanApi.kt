package me.devnatan.katan.api.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@RequiresOptIn(
    "This is an internal Katan API that should not be used from outside of Katan Core module.",
    RequiresOptIn.Level.ERROR
)
annotation class InternalKatanApi
