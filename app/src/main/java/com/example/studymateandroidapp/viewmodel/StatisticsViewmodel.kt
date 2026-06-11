package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Loads real data from Room via StatisticsRepository with second-level precision.
 */
class StatisticsViewmodel(
    private val repository: StatisticsRepository
) : ViewModel() {

    // ── UI State ──────────────────────────────────────────

    data class StatisticsUiState(
        val totalTasks: Int = 0,
        val completedTasks: Int = 0,
        val taskCompletionRate: Int = 0,
        val todayStudySeconds: Int = 0,
        val completedGoals: Int = 0,
        val thisWeekStudySeconds: Int = 0,
        val currentStreak: Int = 0,
        val bestStreak: Int = 0,
        val weeklyStreakStatus: List<Boolean> = emptyList(),
        val weeklyAverageSubtitle: String = "Avg: 0s/day",
        val dailyChartData: List<DailyChartPoint> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    data class DailyChartPoint(
        val label: String,   // "Mon", "Tue" …
        val seconds: Int,    // Use seconds for exact scaling
        val date: LocalDate
    )

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadOverviewStats()
        loadTodaySeconds()
        loadThisWeekSeconds()
        loadStreaks()
        loadDailyChart()
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ── Private loaders ───────────────────────────────────

    private fun loadOverviewStats() {
        viewModelScope.launch {
            repository.getOverviewStats()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message, isLoading = false) } }
                .collect { stats ->
                    _uiState.update {
                        it.copy(
                            totalTasks        = stats.totalTasks,
                            completedTasks    = stats.completedTasks,
                            taskCompletionRate = stats.taskCompletionRate,
                            completedGoals    = stats.completedGoals,
                            isLoading         = false
                        )
                    }
                }
        }
    }

    private fun loadTodaySeconds() {
        viewModelScope.launch {
            repository.getTodayStudySeconds()
                .catch { /* silent */ }
                .collect { seconds ->
                    _uiState.update { it.copy(todayStudySeconds = seconds) }
                }
        }
    }

    private fun loadThisWeekSeconds() {
        viewModelScope.launch {
            repository.getThisWeekStudySeconds()
                .catch { /* silent */ }
                .collect { seconds ->
                    val avgSeconds = seconds / 7
                    val formattedAvg = StatisticsRepository.formatDuration(avgSeconds)
                    _uiState.update { 
                        it.copy(
                            thisWeekStudySeconds = seconds,
                            weeklyAverageSubtitle = "Avg: $formattedAvg/day"
                        ) 
                    }
                }
        }
    }

    private fun loadStreaks() {
        viewModelScope.launch {
            repository.getStreak().collect { streak ->
                _uiState.update { it.copy(currentStreak = streak) }
            }
        }
        viewModelScope.launch {
            repository.getBestStreak().collect { best ->
                _uiState.update { it.copy(bestStreak = best) }
            }
        }
        viewModelScope.launch {
            repository.getWeeklyStreakStatus().collect { status ->
                _uiState.update { it.copy(weeklyStreakStatus = status) }
            }
        }
    }

    private fun loadDailyChart() {
        viewModelScope.launch {
            repository.getDailyStudyData(days = 7)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { dailyData ->
                    val points = dailyData.map { data ->
                        DailyChartPoint(
                            label = data.date.dayOfWeek.name
                                .take(3)
                                .lowercase()
                                .replaceFirstChar { it.uppercase() },
                            seconds = data.studySeconds,
                            date    = data.date
                        )
                    }
                    _uiState.update { it.copy(dailyChartData = points) }
                }
        }
    }
}
