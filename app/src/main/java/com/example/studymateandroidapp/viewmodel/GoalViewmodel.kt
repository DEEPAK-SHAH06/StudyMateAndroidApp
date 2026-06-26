package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.GoalStatus
import com.example.studymateandroidapp.data.model.GoalSubtask
import com.example.studymateandroidapp.data.model.CelebrationEvent
import com.example.studymateandroidapp.data.model.CelebrationType
import com.example.studymateandroidapp.data.repository.GoalRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate

/**
 * ViewModel for Goal List + Add/Edit Goal screens.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GoalViewmodel(
    private val repository: GoalRepository,
    private val motivationRepository: MotivationRepository
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
        val subtasks: List<GoalSubtask> = emptyList(),
        val currentValue: Int = 0,
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

    private val _filter = MutableStateFlow(GoalFilter.ACTIVE)
    val filter: StateFlow<GoalFilter> = _filter.asStateFlow()

    private val _uiSideEffects = MutableStateFlow(GoalListUiSideEffects())
    
    data class GoalListUiSideEffects(
        val errorMessage: String? = null,
        val showEncouragement: Boolean = false,
        val encouragementMessage: String = "",
        val showCelebration: Boolean = false
    )

    val listState: StateFlow<GoalListUiState> = combine(_filter, _uiSideEffects) { currentFilter, sideEffects ->
        currentFilter to sideEffects
    }.flatMapLatest { (currentFilter, sideEffects) ->
        val flow = when (currentFilter) {
            GoalFilter.ACTIVE -> repository.getActiveGoals()
            GoalFilter.ALL, GoalFilter.COMPLETED -> repository.getAllGoals()
        }
        flow.map { goals ->
            val filtered = if (currentFilter == GoalFilter.COMPLETED) {
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

            GoalListUiState(
                goals = displayItems,
                filter = currentFilter,
                isLoading = false,
                errorMessage = sideEffects.errorMessage,
                showEncouragement = sideEffects.showEncouragement,
                encouragementMessage = sideEffects.encouragementMessage,
                showCelebration = sideEffects.showCelebration
            )
        }.catch { e ->
            emit(GoalListUiState(errorMessage = e.message, isLoading = false, filter = currentFilter))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalListUiState(isLoading = true)
    )

    private val _formState = MutableStateFlow(GoalFormUiState())
    val formState: StateFlow<GoalFormUiState> = _formState.asStateFlow()

    // ── List Event Handlers ───────────────────────────────

    fun onFilterChanged(filter: GoalFilter) {
        _filter.value = filter
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
                _uiSideEffects.update {
                    it.copy(showEncouragement = true, encouragementMessage = msg, showCelebration = true)
                }
            }
        }
    }

    fun onToggleSubtaskInList(goalId: Long, subtaskIndex: Int) {
        viewModelScope.launch {
            val goal = repository.getGoalById(goalId).firstOrNull() ?: return@launch
            val updatedSubtasks = goal.subtasks.toMutableList()
            if (subtaskIndex in updatedSubtasks.indices) {
                val subtask = updatedSubtasks[subtaskIndex]
                updatedSubtasks[subtaskIndex] = subtask.copy(isCompleted = !subtask.isCompleted)
            }
            
            val newCurrentValue = updatedSubtasks.count { it.isCompleted }
            val newTargetValue = updatedSubtasks.size
            
            val allCompleted = newTargetValue > 0 && newCurrentValue == newTargetValue
            val newStatus = if (allCompleted) GoalStatus.COMPLETED else GoalStatus.IN_PROGRESS
            
            val awardXp = allCompleted && !goal.isXpAwarded
            
            val updatedGoal = goal.copy(
                subtasks = updatedSubtasks,
                currentValue = newCurrentValue,
                targetValue = newTargetValue,
                status = newStatus,
                lastUpdated = System.currentTimeMillis()
            )
            
            val finalGoal = if (awardXp) {
                motivationRepository.addXp(15, "Goal Completed!")
                motivationRepository.triggerCelebration(
                    CelebrationEvent(
                        type = CelebrationType.GOAL_COMPLETED,
                        title = "Goal Completed",
                        subtitle = goal.title,
                        xpReward = 15,
                        icon = "🎯"
                    )
                )
                motivationRepository.recordStudyActivity()
                updatedGoal.copy(isXpAwarded = true)
            } else {
                updatedGoal
            }
            
            repository.update(finalGoal)
        }
    }

    fun onCompleteGoalDirectly(goalId: Long) {
        viewModelScope.launch {
            val goal = repository.getGoalById(goalId).firstOrNull() ?: return@launch
            val updatedGoal = goal.copy(
                currentValue = goal.targetValue,
                status = GoalStatus.COMPLETED,
                lastUpdated = System.currentTimeMillis()
            )
            val awardXp = !goal.isXpAwarded
            val finalGoal = if (awardXp) {
                motivationRepository.addXp(15, "Goal Completed!")
                motivationRepository.triggerCelebration(
                    CelebrationEvent(
                        type = CelebrationType.GOAL_COMPLETED,
                        title = "Goal Completed",
                        subtitle = goal.title,
                        xpReward = 15,
                        icon = "🎯"
                    )
                )
                motivationRepository.recordStudyActivity()
                updatedGoal.copy(isXpAwarded = true)
            } else {
                updatedGoal
            }
            repository.update(finalGoal)
        }
    }

    fun dismissEncouragement() {
        _uiSideEffects.update { it.copy(showEncouragement = false, showCelebration = false, encouragementMessage = "") }
    }

    fun onDeleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteById(goalId)
        }
    }

    fun onListErrorDismissed() {
        _uiSideEffects.update { it.copy(errorMessage = null) }
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
                        subtasks = goal.subtasks,
                        currentValue = goal.currentValue,
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
    // CheckList Handler

    fun onAddSubtask(title: String) {
        if (title.isBlank()) return
        _formState.update { state ->
            val newList = state.subtasks + GoalSubtask(title = title)
            state.copy(
                subtasks = newList,
                targetValue = newList.size,
                currentValue = newList.count { it.isCompleted }
            )
        }
    }

    fun onToggleSubtask(index: Int) {
        _formState.update { state ->
            val newList = state.subtasks.toMutableList()
            val item = newList[index]
            newList[index] = item.copy(isCompleted = !item.isCompleted)
            
            val newCurrentValue = newList.count { it.isCompleted }
            
            state.copy(
                subtasks = newList,
                currentValue = newCurrentValue
            )
        }
    }

    fun onRemoveSubtask(index: Int) {
        _formState.update { state ->
            val newList = state.subtasks.toMutableList()
            newList.removeAt(index)
            state.copy(
                subtasks = newList,
                targetValue = newList.size,
                currentValue = newList.count { it.isCompleted }
            )
        }
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
                // Determine status based on completion
                val newStatus = if (state.targetValue > 0 && state.currentValue >= state.targetValue) {
                    GoalStatus.COMPLETED 
                } else {
                    GoalStatus.IN_PROGRESS
                }

                if (state.isEditMode) {
                    repository.update(
                        Goal(
                            id = state.goalId!!,
                            title = state.title.trim(),
                            status = newStatus,
                            description = state.description.trim(),
                            targetValue = state.targetValue,
                            currentValue = state.currentValue,
                            subtasks = state.subtasks,
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
                            status = newStatus,
                            currentValue = state.currentValue,
                            subtasks = state.subtasks,
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
}
