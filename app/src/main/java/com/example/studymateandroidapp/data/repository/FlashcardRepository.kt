package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.FlashcardDao
import com.example.studymateandroidapp.data.model.Flashcard
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    val allFlashcards: Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()

    fun getFlashcardsByExamId(examId: Long): Flow<List<Flashcard>> =
        flashcardDao.getFlashcardsByExamId(examId)

    suspend fun getFlashcardById(id: Long): Flashcard? =
        flashcardDao.getFlashcardById(id)

    suspend fun insert(flashcard: Flashcard) {
        flashcardDao.insert(flashcard)
    }

    suspend fun update(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }

    suspend fun delete(flashcard: Flashcard) {
        flashcardDao.delete(flashcard)
    }

    suspend fun completeReviewSession(examId: Long, correctCount: Int, cardsCount: Int) {
        flashcardDao.insertReview(
            com.example.studymateandroidapp.data.model.FlashcardReview(
                examId = examId,
                correctCount = correctCount,
                cardsReviewed = cardsCount
            )
        )
    }
}
