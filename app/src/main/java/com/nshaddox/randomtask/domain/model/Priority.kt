package com.nshaddox.randomtask.domain.model

/**
 * Represents the priority level of a [Task].
 *
 * Tasks default to [MEDIUM] priority when no explicit priority is provided.
 */
enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
}
