package me.devnatan.katan.api.server

/**
 * Represents the result of the inspection of a container.
 * @see ServerContainer
 */
interface ServerContainerInspection {

    /**
     * Represents an inspection result not yet performed.
     */
    object NotInspected : ServerContainerInspection {

        override fun toString(): String {
            return "Not inspected yet."
        }

    }

}