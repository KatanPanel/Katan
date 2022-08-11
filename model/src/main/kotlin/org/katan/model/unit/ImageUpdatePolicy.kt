package org.katan.model.unit

enum class ImageUpdatePolicy(val id: String) {

    Always("always"),
    Never("never");

    companion object {

        @JvmStatic
        fun getById(id: String): ImageUpdatePolicy {
            return values().first { it.id.equals(id, ignoreCase = true) }
        }
    }
}
