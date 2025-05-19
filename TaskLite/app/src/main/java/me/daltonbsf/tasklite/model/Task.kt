package me.daltonbsf.tasklite.model

data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean = false
)

enum class TaskFilter {
    ALL, COMPLETED, PENDING
}