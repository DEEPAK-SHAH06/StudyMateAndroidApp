package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Records a completed flashcard review session.
 */
@Entity(tableName = "flashcard_reviews")
data class FlashcardReview(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val date: LocalDate = LocalDate.now(),
    val cardsReviewed: Int,
    val correctCount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
