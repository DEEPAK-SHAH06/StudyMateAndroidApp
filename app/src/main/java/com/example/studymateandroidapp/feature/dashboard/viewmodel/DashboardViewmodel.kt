package com.example.studymateandroidapp.feature.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.core.model.Exam
import com.example.studymateandroidapp.core.model.Goal
import com.example.studymateandroidapp.core.model.Task
import com.example.studymateandroidapp.feature.exams.data.ExamRepository
import com.example.studymateandroidapp.feature.goals.data.GoalRepository
import com.example.studymateandroidapp.feature.sessions.data.SessionRepository
import com.example.studymateandroidapp.feature.tasks.data.TaskRepository
import com.example.studymateandroidapp.core.auth.AuthRepository
import com.example.studymateandroidapp.core.preferences.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * ViewModel for the Home Dashboard screen.
 *
 * Responsibilities:
 * - Show today's pending tasks
 * - Show today's study minutes
 * - Show the next upcoming exam with countdown
 * - Show active goals with progress
 * - Provide a greeting based on time of day
 *
 * This is a cross-cutting ViewModel that reads from multiple repositories
 * but never imports other ViewModels.
 */
class DashboardViewModel(
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val examRepository: ExamRepository,
    private val goalRepository: GoalRepository,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // ── UI State ──────────────────────────────────────────

    data class DashboardUiState(
        val greeting: String = "",
        val todayDate: String = "",
        val userName: String = "",
        val userBio: String = "",

        // Tasks
        val todayTasks: List<Task> = emptyList(),
        val pendingTaskCount: Int = 0,

        // Study
        val todayStudyMinutes: Int = 0,

        // Next exam
        val nextExam: Exam? = null,
        val daysUntilNextExam: Long? = null,

        // Goals
        val activeGoals: List<GoalSummary> = emptyList(),

        // Motivation
        val dailyQuote: String = "",
        val dailyQuoteAuthor: String = "",
        val currentStreak: Int = 0,
        val showReflectionPrompt: Boolean = false,
        val newAchievements: List<String> = emptyList(),
        val showCelebration: Boolean = false,
        val celebrationMessage: String = "",

        // General
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val isLoggedIn: Boolean = false,
        val showSyncPrompt: Boolean = false
    ) {
        val todayStudyFormatted: String
            get() {
                val h = todayStudyMinutes / 60
                val m = todayStudyMinutes % 60
                return if (h > 0) "${h}h ${m}m" else "${m}m"
            }
    }

    data class GoalSummary(
        val id: Long,
        val title: String,
        val progressPercent: Int
    )

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        setGreeting()
        loadDailyQuote()
        loadTodayTasks()
        loadTodayStudyMinutes()
        loadNextExam()
        loadActiveGoals()
        observeAuthState()
        checkReflectionPrompt()
        observeProfile()
    }

    // ── Event Handlers ────────────────────────────────────

    fun onTaskCompletionToggled(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.setCompleted(taskId, completed)
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false, newAchievements = emptyList(), celebrationMessage = "") }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        isLoggedIn = user != null,
                        showSyncPrompt = user == null
                    )
                }
            }
        }
    }

    // ── Private ───────────────────────────────────────────

    private fun loadDailyQuote() {
        val today = java.time.LocalDate.now()
        val (quote, author) = com.example.studymateandroidapp.core.model.MotivationalQuotes.getQuoteForDate(today.toEpochDay())
        _uiState.update { it.copy(dailyQuote = quote, dailyQuoteAuthor = author) }
    }

    private fun checkReflectionPrompt() {
        val hour = java.time.LocalTime.now().hour
        _uiState.update { it.copy(showReflectionPrompt = hour >= 17) }
    }

    private fun setGreeting() {
        val hour = java.time.LocalTime.now().hour
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        val today = LocalDate.now()
        val dateStr = "${today.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${today.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${today.dayOfMonth}"

        _uiState.update { it.copy(greeting = greeting, todayDate = dateStr) }
    }

    private fun loadTodayTasks() {
        viewModelScope.launch {
            taskRepository.getTasksDueOn(LocalDate.now())
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { tasks ->
                    _uiState.update {
                        it.copy(
                            todayTasks = tasks,
                            pendingTaskCount = tasks.count { t -> !t.isCompleted },
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadTodayStudyMinutes() {
        viewModelScope.launch {
            sessionRepository.getStudyMinutesForDate(LocalDate.now())
                .catch { /* silently ignore */ }
                .collect { minutes ->
                    _uiState.update { it.copy(todayStudyMinutes = minutes) }
                }
        }
    }

    private fun loadNextExam() {
        viewModelScope.launch {
            examRepository.getAllExams()
                .catch { /* silently ignore */ }
                .collect { exams ->
                    val today = LocalDate.now()
                    val nextExam = exams
                        .filter {
                            !java.time.Instant.ofEpochMilli(it.examDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate().isBefore(today)
                        }
                        .minByOrNull { it.examDate }

                    val days = nextExam?.let {
                        val examDateLocal = java.time.Instant.ofEpochMilli(it.examDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        ChronoUnit.DAYS.between(today, examDateLocal)
                    }
                    _uiState.update {
                        it.copy(nextExam = nextExam, daysUntilNextExam = days)
                    }
                }
        }
    }

    private fun loadActiveGoals() {
        viewModelScope.launch {
            goalRepository.getActiveGoals()
                .catch { /* silently ignore */ }
                .collect { goals ->
                    val summaries = goals.map { goal ->
                        GoalSummary(
                            id = goal.id,
                            title = goal.title,
                            progressPercent = if (goal.targetValue > 0) {
                                ((goal.currentValue * 100) / goal.targetValue).coerceIn(0, 100)
                            } else 0
                        )
                    }
                    _uiState.update { it.copy(activeGoals = summaries) }
                }
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            preferenceManager.userName.collect { name ->
                _uiState.update { it.copy(userName = name) }
            }
        }
        viewModelScope.launch {
            preferenceManager.userBio.collect { bio ->
                _uiState.update { it.copy(userBio = bio) }
            }
        }
    }
}
