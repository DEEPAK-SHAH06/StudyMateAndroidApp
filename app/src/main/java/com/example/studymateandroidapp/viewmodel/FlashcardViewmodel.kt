package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.repository.FlashcardRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardViewmodel(
    private val repository: FlashcardRepository,
    private val motivationRepository: MotivationRepository
) : ViewModel() {

    val allFlashcards: StateFlow<List<Flashcard>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getFlashcardsByExamId(examId: Long): StateFlow<List<Flashcard>> =
        repository.getFlashcardsByExamId(examId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getFlashcardById(id: Long): Flashcard? = repository.getFlashcardById(id)

    fun addFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.insert(flashcard)
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.update(flashcard)
        }
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.delete(flashcard)
        }
    }

    fun completeReviewSession(cardsCount: Int) {
        viewModelScope.launch {
            repository.completeReviewSession(cardsCount)
            motivationRepository.recordStudyActivity()
        }
    }
}
