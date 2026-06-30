package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a study note linked to an exam.
 */
@Entity(
    tableName = "notes",
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
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val content: String,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
