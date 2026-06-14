package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tracks study progress and mastery for a specific exam.
 */
@Entity(
    tableName = "study_progress",
    foreignKeys = [
        ForeignKey(
            entity = Exam::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("examId")]
)
data class StudyProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val totalStudyTime: Long = 0, // In milliseconds
    val flashcardMastery: Float = 0f, // 0.0 to 1.0
    val completionPercentage: Float = 0f, // 0.0 to 1.0
    val lastStudiedTimestamp: Long = System.currentTimeMillis()
)
