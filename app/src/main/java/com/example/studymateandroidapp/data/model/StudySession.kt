package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Represents a completed (or in-progress) study session.
 *
 * Optionally linked to a [Task] via [taskId] (nullable FK).
 * Deleting the parent Task sets [taskId] to null.
 */
@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Exam::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("taskId"), Index("examId"), Index("startTime"), Index("userId"), Index("serverId")]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long? = null,
    val examId: Long? = null,
    val subject: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val durationMinutes: Int = 0,
    val notes: String = "",

    // Sync metadata
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
