package com.example.studymateandroidapp.feature.flashcard.data

import com.example.studymateandroidapp.core.model.Flashcard
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    val allFlashcards: Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()

    suspend fun insert(flashcard: Flashcard) {
        flashcardDao.insert(flashcard)
    }

    suspend fun update(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }

    suspend fun delete(flashcard: Flashcard) {
        flashcardDao.delete(flashcard)
    }
}
