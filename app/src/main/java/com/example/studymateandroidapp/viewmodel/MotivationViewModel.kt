package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Achievement
import com.example.studymateandroidapp.data.model.DailyReflection
import com.example.studymateandroidapp.data.model.MotivationalQuotes
import com.example.studymateandroidapp.data.repository.MotivationRepository
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
        val todayReflection: DailyReflection? = null,
        val reflectionContent: String = "",
        val reflectionMood: String = "\uD83D\uDE0A",
        val reflectionHighlight: String = "",
        val editingReflection: DailyReflection? = null,
        val showReflectionPrompt: Boolean = false,
        val isReflectionSaved: Boolean = false
    )

    private val _uiState = MutableStateFlow(MotivationUiState())
    val uiState = _uiState.asStateFlow()

    val allAchievements = repository.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievementProgress = repository.getAchievementProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentReflections = repository.getRecentReflections(30)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadDailyQuote()
        loadTodayReflection()
        checkShowReflectionPrompt()
        observeStreak()
    }

    private fun observeStreak() {
        viewModelScope.launch {
            repository.getStreak().collect { streak ->
                _uiState.update { it.copy(currentStreak = streak) }
            }
        }
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
                    reflectionMood = reflection?.mood ?: "\uD83D\uDE0A",
                    reflectionHighlight = reflection?.studyHighlight ?: "",
                    editingReflection = null
                )
            }
        }
    }

    private fun checkShowReflectionPrompt() {
        val hour = java.time.LocalTime.now().hour
        _uiState.update { it.copy(showReflectionPrompt = hour >= 17) }
    }

    fun checkAndUnlockAchievements() {
        viewModelScope.launch {
            repository.checkAndUnlockAchievements()
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
            val editingReflection = state.editingReflection
            val reflection = DailyReflection(
                id = editingReflection?.id ?: state.todayReflection?.id ?: 0,
                date = editingReflection?.date ?: today.toEpochDay(),
                content = state.reflectionContent,
                mood = state.reflectionMood,
                studyHighlight = state.reflectionHighlight
            )

            repository.saveReflection(reflection)

            val savedToday = reflection.date == today.toEpochDay()
            _uiState.update {
                it.copy(
                    todayReflection = if (savedToday) reflection else it.todayReflection,
                    editingReflection = null,
                    isReflectionSaved = true
                )
            }
            checkAndUnlockAchievements()
        }
    }

    fun editReflection(reflection: DailyReflection) {
        _uiState.update {
            it.copy(
                editingReflection = reflection,
                reflectionContent = reflection.content,
                reflectionMood = reflection.mood,
                reflectionHighlight = reflection.studyHighlight
            )
        }
    }

    fun deleteReflection(reflection: DailyReflection) {
        viewModelScope.launch {
            repository.deleteReflection(reflection)
            val today = LocalDate.now().toEpochDay()
            _uiState.update {
                if (reflection.date == today) {
                    it.copy(
                        todayReflection = null,
                        editingReflection = null,
                        reflectionContent = "",
                        reflectionMood = "\uD83D\uDE0A",
                        reflectionHighlight = ""
                    )
                } else if (it.editingReflection?.id == reflection.id) {
                    it.copy(
                        editingReflection = null,
                        reflectionContent = it.todayReflection?.content ?: "",
                        reflectionMood = it.todayReflection?.mood ?: "\uD83D\uDE0A",
                        reflectionHighlight = it.todayReflection?.studyHighlight ?: ""
                    )
                } else {
                    it
                }
            }
        }
    }

    fun resetReflectionSaved() {
        _uiState.update { it.copy(isReflectionSaved = false) }
    }

    fun getEncouragementMessage(): String = repository.getEncouragementMessage()
}
