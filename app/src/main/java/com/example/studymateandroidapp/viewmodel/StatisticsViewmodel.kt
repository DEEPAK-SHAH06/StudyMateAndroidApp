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
 * Loads real data from Room via StatisticsRepository.
 */
class StatisticsViewmodel(
    private val repository: StatisticsRepository
) : ViewModel() {

    // ── UI State ──────────────────────────────────────────

    data class StatisticsUiState(
        val totalTasks: Int = 0,
        val completedTasks: Int = 0,
        val taskCompletionRate: Int = 0,
        val totalStudyMinutes: Int = 0,
        val formattedStudyTime: String = "0h 0m",
        val completedGoals: Int = 0,
        val todayStudyMinutes: Int = 0,
        val weekStudyMinutes: Int = 0,
        val dailyChartData: List<DailyChartPoint> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    data class DailyChartPoint(
        val label: String,   // "Mon", "Tue" …
        val minutes: Int,
        val date: LocalDate
    )

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadOverviewStats()
        loadTodayMinutes()
        loadWeekMinutes()
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
                            totalStudyMinutes = stats.totalStudyMinutes,
                            formattedStudyTime = stats.formattedStudyTime,
                            completedGoals    = stats.completedGoals,
                            isLoading         = false
                        )
                    }
                }
        }
    }

    private fun loadTodayMinutes() {
        viewModelScope.launch {
            repository.getTodayStudyMinutes()
                .catch { /* silent */ }
                .collect { minutes ->
                    _uiState.update { it.copy(todayStudyMinutes = minutes) }
                }
        }
    }

    private fun loadWeekMinutes() {
        viewModelScope.launch {
            repository.getThisWeekStudyMinutes()
                .catch { /* silent */ }
                .collect { minutes ->
                    _uiState.update { it.copy(weekStudyMinutes = minutes) }
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
                            minutes = data.studyMinutes,
                            date    = data.date
                        )
                    }
                    _uiState.update { it.copy(dailyChartData = points) }
                }
        }
    }
}