package org.katan.model.unit

public enum class ImageUpdatePolicy(public val id: String) {

    Always("always"),
    Never("never");

    public companion object {

        @JvmStatic
        public fun getById(id: String): ImageUpdatePolicy =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: Always
    }
}
