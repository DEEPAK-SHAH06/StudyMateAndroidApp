package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.StudyProgress
import com.example.studymateandroidapp.data.repository.FlashcardRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository
import com.example.studymateandroidapp.data.repository.StudyProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class FlashcardViewmodel(
    private val repository: FlashcardRepository,
    private val motivationRepository: MotivationRepository,
    private val studyProgressRepository: StudyProgressRepository
) : ViewModel() {

    val allFlashcards: StateFlow<List<Flashcard>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getFlashcardsByExamId(examId: Long): StateFlow<List<Flashcard>> =
        repository.getFlashcardsByExamId(examId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getFlashcardById(id: Long): Flashcard? = repository.getFlashcardById(id)

    fun completeFlashcardSession(examId: Long, correctCount: Int, totalCount: Int) {
        viewModelScope.launch {
            val performance =
                if (totalCount > 0) correctCount.toFloat() / totalCount else 0f

            studyProgressRepository.getProgressByExamId(examId)
                .take(1)
                .collect 
            { current ->
                val progress = current ?: StudyProgress(examId = examId)

                val newFlashcardMastery =
                    if (progress.flashcardMastery == 0f)
                        performance
                    else
                        (progress.flashcardMastery + performance) / 2f

                val timePoints =
                    (progress.totalStudyTime.toFloat() /
                            (5 * 60 * 60 * 1000))
                        .coerceAtMost(1f) * 0.3f

                val performancePoints = newFlashcardMastery * 0.7f

                val newPercentage =
                    (timePoints + performancePoints)
                        .coerceIn(0f, 1f)

                studyProgressRepository.updateProgress(
                    progress.copy(
                        flashcardMastery = newFlashcardMastery,
                        completionPercentage = newPercentage,
                        lastStudiedTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

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
