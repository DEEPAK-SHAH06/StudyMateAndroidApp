package com.example.studymateandroidapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.repository.FlashcardRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.studymateandroidapp.data.model.CelebrationEvent
import com.example.studymateandroidapp.data.model.CelebrationType

class FlashcardViewmodel(
    private val repository: FlashcardRepository,
    private val motivationRepository: MotivationRepository,
    private val application: Application
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
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.update(flashcard)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.delete(flashcard)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun completeFlashcardSession(examId: Long, correct: Int, total: Int) {
        viewModelScope.launch {
            repository.completeReviewSession(examId, correct, total)
            motivationRepository.addXp(3, "Flashcard Review Complete!")
            motivationRepository.triggerCelebration(
                CelebrationEvent(
                    type = CelebrationType.TASK_COMPLETED,
                    title = "Flashcard Review Complete",
                    subtitle = "$correct of $total correct",
                    xpReward = 3,
                    icon = "🧠"
                )
            )
        }
    }
}
