package me.devnatan.katan.api.annotations

@Retention(AnnotationRetention.RUNTIME)
@RequiresOptIn(
    "This is an unstable Katan API, it may be subject to changes in the future that may affect your code, and, because it is unstable, it is not guaranteed to work properly.",
    RequiresOptIn.Level.WARNING
)
annotation class UnstableKatanApi
