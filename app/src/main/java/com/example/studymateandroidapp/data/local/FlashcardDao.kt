package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.FlashcardReview
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE examId = :examId")
    fun getFlashcardsByExamId(examId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getFlashcardById(id: Long): Flashcard?

    @Query("SELECT COUNT(*) FROM flashcards")
    fun getFlashcardCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: Flashcard): Long

    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)

    @Query("SELECT * FROM flashcards")
    suspend fun getAllFlashcardsList(): List<Flashcard>

    // ── Review Sessions ───────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: FlashcardReview): Long

    @Query("SELECT DISTINCT date FROM flashcard_reviews")
    fun getReviewDates(): Flow<List<LocalDate>>
}
