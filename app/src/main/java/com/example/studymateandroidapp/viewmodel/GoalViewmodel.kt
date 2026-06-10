package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.GoalStatus
import com.example.studymateandroidapp.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for Goal List + Add/Edit Goal screens.
 */
class GoalViewmodel(
    private val repository: GoalRepository
) : ViewModel() {

    // ── List UI State ─────────────────────────────────────

    data class GoalListUiState(
        val goals: List<GoalDisplayItem> = emptyList(),
        val filter: GoalFilter = GoalFilter.ACTIVE,
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val showEncouragement: Boolean = false,
        val encouragementMessage: String = "",
        val showCelebration: Boolean = false
    )

    data class GoalDisplayItem(
        val goal: Goal,
        val progressPercent: Int
    )

    enum class GoalFilter { ALL, ACTIVE, COMPLETED }

    // ── Form UI State ─────────────────────────────────────

    data class GoalFormUiState(
        val goalId: Long? = null,
        val title: String = "",
        val description: String = "",
        val targetValue: Int = 100,
        val examId: Long? = null,
        val deadline: LocalDate? = null,
        val isLoading: Boolean = false,
        val isSaved: Boolean = false,
        val titleError: String? = null,
        val errorMessage: String? = null
    ) {
        val isEditMode: Boolean get() = goalId != null
    }

    private val _listState = MutableStateFlow(GoalListUiState())
    val listState: StateFlow<GoalListUiState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(GoalFormUiState())
    val formState: StateFlow<GoalFormUiState> = _formState.asStateFlow()

    init {
        loadGoals()
    }

    // ── List Event Handlers ───────────────────────────────

    fun onFilterChanged(filter: GoalFilter) {
        _listState.update { it.copy(filter = filter) }
        loadGoals()
    }

    fun onUpdateProgress(goalId: Long, newValue: Int) {
        viewModelScope.launch {
            val goal = repository.getGoalById(goalId).firstOrNull()
            repository.updateProgress(goalId, newValue)
            // Check if goal just became complete
            if (goal != null && newValue >= goal.targetValue && goal.currentValue < goal.targetValue) {
                val msg = listOf(
                    "🎯 Incredible! Goal crushed!",
                    "🌟 You're unstoppable! Another goal done!",
                    "🏆 Champion! Keep going!",
                    "🚀 Goal achieved! What's next?",
                    "💪 You earned this — brilliant work!"
                ).random()
                _listState.update {
                    it.copy(showEncouragement = true, encouragementMessage = msg, showCelebration = true)
                }
            }
        }
    }

    fun dismissEncouragement() {
        _listState.update { it.copy(showEncouragement = false, showCelebration = false, encouragementMessage = "") }
    }

    fun onDeleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteById(goalId)
        }
    }

    fun onListErrorDismissed() {
        _listState.update { it.copy(errorMessage = null) }
    }

    // ── Form Event Handlers ───────────────────────────────

    suspend fun getGoalById(id: Long): Goal? {
        return repository.getGoalById(id).firstOrNull()
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insert(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.update(goal)
        }
    }

    fun loadGoalForEdit(goalId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            val goal = repository.getGoalById(goalId).firstOrNull()
            if (goal != null) {
                _formState.update {
                    it.copy(
                        goalId = goal.id,
                        title = goal.title,
                        description = goal.description,
                        targetValue = goal.targetValue,
                        examId = goal.examId,
                        deadline = goal.deadline,
                        isLoading = false
                    )
                }
            } else {
                _formState.update {
                    it.copy(errorMessage = "Goal not found", isLoading = false)
                }
            }
        }
    }

    fun resetForm() {
        _formState.value = GoalFormUiState()
    }

    fun onTitleChanged(title: String) {
        _formState.update { it.copy(title = title, titleError = null) }
    }

    fun onDescriptionChanged(description: String) {
        _formState.update { it.copy(description = description) }
    }

    fun onTargetValueChanged(value: Int) {
        _formState.update { it.copy(targetValue = value.coerceAtLeast(1)) }
    }

    fun onExamIdChanged(examId: Long?) {
        _formState.update { it.copy(examId = examId) }
    }

    fun onDeadlineChanged(deadline: LocalDate?) {
        _formState.update { it.copy(deadline = deadline) }
    }

    fun onSaveGoal() {
        val state = _formState.value

        if (state.title.isBlank()) {
            _formState.update { it.copy(titleError = "Title is required") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            try {
                if (state.isEditMode) {
                    repository.update(
                        Goal(
                            id = state.goalId!!,
                            title = state.title.trim(),
                            description = state.description.trim(),
                            targetValue = state.targetValue,
                            examId = state.examId,
                            deadline = state.deadline
                        )
                    )
                } else {
                    repository.insert(
                        Goal(
                            title = state.title.trim(),
                            description = state.description.trim(),
                            targetValue = state.targetValue,
                            examId = state.examId,
                            deadline = state.deadline
                        )
                    )
                }
                _formState.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(errorMessage = e.message, isLoading = false)
                }
            }
        }
    }

    fun onFormErrorDismissed() {
        _formState.update { it.copy(errorMessage = null) }
    }

    private fun loadGoals() {
        viewModelScope.launch {
            val flow = when (_listState.value.filter) {
                GoalFilter.ACTIVE -> repository.getActiveGoals()
                GoalFilter.ALL, GoalFilter.COMPLETED -> repository.getAllGoals()
            }

            flow.catch { e ->
                _listState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }.collect { goals ->
                val filtered = if (_listState.value.filter == GoalFilter.COMPLETED) {
                    goals.filter { it.status == GoalStatus.COMPLETED }
                } else {
                    goals
                }

                val displayItems = filtered.map { goal ->
                    GoalDisplayItem(
                        goal = goal,
                        progressPercent = if (goal.targetValue > 0) {
                            ((goal.currentValue * 100) / goal.targetValue).coerceIn(0, 100)
                        } else 0
                    )
                }

                _listState.update {
                    it.copy(goals = displayItems, isLoading = false)
                }
            }
        }
    }
}
