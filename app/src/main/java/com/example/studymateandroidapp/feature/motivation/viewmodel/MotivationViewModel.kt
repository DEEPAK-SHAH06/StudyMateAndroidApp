package com.example.studymateandroidapp.feature.motivation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.core.model.Achievement
import com.example.studymateandroidapp.core.model.DailyReflection
import com.example.studymateandroidapp.core.model.MotivationalQuotes
import com.example.studymateandroidapp.feature.motivation.data.MotivationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MotivationViewModel(
    private val repository: MotivationRepository
) : ViewModel() {

    data class MotivationUiState(
        val dailyQuote: String = "",
        val dailyQuoteAuthor: String = "",
        val currentStreak: Int = 0,
        val newAchievements: List<Achievement> = emptyList(),
        val showCelebration: Boolean = false,
        val celebrationMessage: String = "",
        val todayReflection: DailyReflection? = null,
        val reflectionContent: String = "",
        val reflectionMood: String = "😊",
        val reflectionHighlight: String = "",
        val showReflectionPrompt: Boolean = false,
        val isReflectionSaved: Boolean = false
    )

    private val _uiState = MutableStateFlow(MotivationUiState())
    val uiState = _uiState.asStateFlow()

    val allAchievements = repository.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentReflections = repository.getRecentReflections(30)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadDailyQuote()
        loadTodayReflection()
        checkShowReflectionPrompt()
    }

    private fun loadDailyQuote() {
        val today = LocalDate.now()
        val (quote, author) = MotivationalQuotes.getQuoteForDate(today.toEpochDay())
        _uiState.update { it.copy(dailyQuote = quote, dailyQuoteAuthor = author) }
    }

    private fun loadTodayReflection() {
        viewModelScope.launch {
            val reflection = repository.getReflectionForDate(LocalDate.now())
            _uiState.update {
                it.copy(
                    todayReflection = reflection,
                    reflectionContent = reflection?.content ?: "",
                    reflectionMood = reflection?.mood ?: "😊",
                    reflectionHighlight = reflection?.studyHighlight ?: ""
                )
            }
        }
    }

    private fun checkShowReflectionPrompt() {
        val hour = java.time.LocalTime.now().hour
        // Show reflection prompt in the evening (after 5 PM) if not yet done
        _uiState.update { it.copy(showReflectionPrompt = hour >= 17) }
    }

    fun checkAndUnlockAchievements() {
        viewModelScope.launch {
            val newOnes = repository.checkAndUnlockAchievements()
            if (newOnes.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        newAchievements = newOnes,
                        showCelebration = true,
                        celebrationMessage = "🏆 New Achievement Unlocked: ${newOnes.first().title}!"
                    )
                }
            }
        }
    }

    fun onReflectionContentChanged(content: String) {
        _uiState.update { it.copy(reflectionContent = content) }
    }

    fun onReflectionMoodChanged(mood: String) {
        _uiState.update { it.copy(reflectionMood = mood) }
    }

    fun onReflectionHighlightChanged(highlight: String) {
        _uiState.update { it.copy(reflectionHighlight = highlight) }
    }

    fun saveReflection() {
        viewModelScope.launch {
            val state = _uiState.value
            val today = LocalDate.now()
            val reflection = DailyReflection(
                id = state.todayReflection?.id ?: 0,
                date = today.toEpochDay(),
                content = state.reflectionContent,
                mood = state.reflectionMood,
                studyHighlight = state.reflectionHighlight
            )
            repository.saveReflection(reflection)
            _uiState.update {
                it.copy(
                    todayReflection = reflection,
                    isReflectionSaved = true,
                    showCelebration = true,
                    celebrationMessage = "✍️ Reflection saved! Great self-awareness!"
                )
            }
            // Check achievements after saving reflection
            checkAndUnlockAchievements()
        }
    }

    fun dismissCelebration() {
        _uiState.update {
            it.copy(showCelebration = false, newAchievements = emptyList(), celebrationMessage = "")
        }
    }

    fun resetReflectionSaved() {
        _uiState.update { it.copy(isReflectionSaved = false) }
    }

    fun getEncouragementMessage(): String = repository.getEncouragementMessage()
}
