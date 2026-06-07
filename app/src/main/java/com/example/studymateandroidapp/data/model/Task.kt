package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a study task.
 *
 * Optionally linked to an [Exam] via [examId] (nullable FK).
 * Deleting the parent Exam sets [examId] to null.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Exam::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("examId"), Index("dueDate"), Index("status"), Index("userId"), Index("serverId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: LocalDate? = null,
    val dueTime: java.time.LocalTime? = null,
    val createdAt: LocalDate = LocalDate.now(),
    val examId: Long? = null,
    val subjectTag: String? = null, // Cached for display (e.g., "PSYCHOLOGY")
    val isCompleted: Boolean = false,
    val completedAt: LocalDate? = null,
    
    // Sync metadata
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val isOverdue: Boolean
        get() {
            if (isCompleted) return false
            val date = dueDate ?: return false
            val time = dueTime ?: java.time.LocalTime.MAX
            val dueDateTime = java.time.LocalDateTime.of(date, time)
            return dueDateTime.isBefore(java.time.LocalDateTime.now())
        }
}
