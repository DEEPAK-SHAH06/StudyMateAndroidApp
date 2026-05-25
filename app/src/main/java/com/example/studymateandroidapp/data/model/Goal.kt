package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents an academic goal with measurable progress.
 *
 * Optionally linked to an [Exam] via [examId] (nullable FK).
 * Deleting the parent Exam sets [examId] to null.
 */
@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = Exam::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("examId"), Index("deadline"), Index("userId"), Index("serverId")]
)
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val status: GoalStatus = GoalStatus.NOT_STARTED,
    val targetValue: Int = 100,
    val currentValue: Int = 0,
    val examId: Long? = null,
    val deadline: LocalDate? = null,
    val createdAt: LocalDate = LocalDate.now(),

    // Sync metadata
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
