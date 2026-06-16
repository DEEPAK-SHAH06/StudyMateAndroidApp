package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.repository.*
import com.example.studymateandroidapp.data.local.PreferenceManager
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
 */
class DashboardViewModel(
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val examRepository: ExamRepository,
    private val goalRepository: GoalRepository,
    private val motivationRepository: MotivationRepository,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // ── UI State ──────────────────────────────────────────

    data class DashboardUiState(
        val greeting: String = "",
        val todayDate: String = "",
        val userName: String = "",
        val userBio: String = "",
        val userPhotoUrl: String? = null,

        // Tasks
        val todayTasks: List<Task> = emptyList(),
        val pendingTaskCount: Int = 0,

        // Study
        val todayStudyMinutes: Int = 0,

        // Exams
        val upcomingExams: List<ExamCountdown> = emptyList(),

        // Goals
        val activeGoals: List<GoalSummary> = emptyList(),
        val totalGoalCount: Int = 0,
        val completedGoalCount: Int = 0,

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

    data class ExamCountdown(
        val id: Long,
        val title: String,
        val subject: String,
        val daysUntil: Long,
        val isToday: Boolean
    )

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        setGreeting()
        loadDailyQuote()
        loadTodayTasks()
        loadTodayStudyMinutes()
        loadUpcomingExams()
        loadActiveGoals()
        observeAuthState()
        checkReflectionPrompt()
        observeProfile()
        observeStreak()
    }

    private fun observeStreak() {
        viewModelScope.launch {
            motivationRepository.getStreak().collect { streak ->
                _uiState.update { it.copy(currentStreak = streak) }
            }
        }
    }

    // ── Event Handlers ────────────────────────────────────

    fun onTaskCompletionToggled(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            if (task != null) {
                taskRepository.update(task.copy(
                    isCompleted = completed,
                    status = if (completed) com.example.studymateandroidapp.data.model.TaskStatus.COMPLETED else com.example.studymateandroidapp.data.model.TaskStatus.TODO,
                    completedAt = if (completed) LocalDate.now() else null
                ))
                if (completed) {
                    motivationRepository.recordStudyActivity()
                }
            }
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
                        showSyncPrompt = user == null,
                        userName = user?.displayName ?: it.userName
                    )
                }
            }
        }
    }

    // ── Private ───────────────────────────────────────────

    private fun loadDailyQuote() {
        val today = java.time.LocalDate.now()
        val (quote, author) = com.example.studymateandroidapp.data.model.MotivationalQuotes.getQuoteForDate(today.toEpochDay())
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
            sessionRepository.getStudySecondsForDate(LocalDate.now())
                .catch { /* silently ignore */ }
                .collect { seconds ->
                    _uiState.update { it.copy(todayStudyMinutes = seconds / 60) }
                }
        }
    }

    private fun loadUpcomingExams() {
        viewModelScope.launch {
            examRepository.allExams
                .catch { /* silently ignore */ }
                .collect { exams ->
                    val today = LocalDate.now()
                    val upcoming = exams
                        .filter {
                            val examDateLocal = java.time.Instant.ofEpochMilli(it.examDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            !examDateLocal.isBefore(today)
                        }
                        .sortedBy { it.examDate }
                        .map {
                            val examDateLocal = java.time.Instant.ofEpochMilli(it.examDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            val days = ChronoUnit.DAYS.between(today, examDateLocal)
                            ExamCountdown(
                                id = it.id,
                                title = it.title,
                                subject = it.subject,
                                daysUntil = days,
                                isToday = days == 0L
                            )
                        }
                    _uiState.update {
                        it.copy(upcomingExams = upcoming)
                    }
                }
        }
    }

    private fun loadActiveGoals() {
        viewModelScope.launch {
            goalRepository.getAllGoals()
                .catch { /* silently ignore */ }
                .collect { goals ->
                    val summaries = goals.filter { it.currentValue < it.targetValue }.map { goal ->
                        GoalSummary(
                            id = goal.id,
                            title = goal.title,
                            progressPercent = if (goal.targetValue > 0) {
                                ((goal.currentValue * 100) / goal.targetValue).coerceIn(0, 100)
                            } else 0
                        )
                    }
                    _uiState.update { 
                        it.copy(
                            activeGoals = summaries,
                            totalGoalCount = goals.size,
                            completedGoalCount = goals.count { g -> g.currentValue >= g.targetValue }
                        )
                    }
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
        viewModelScope.launch {
            preferenceManager.userPhotoUri.collect { uri ->
                _uiState.update { it.copy(userPhotoUrl = uri) }
            }
        }
    }
}
