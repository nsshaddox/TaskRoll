package com.nshaddox.randomtask.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
