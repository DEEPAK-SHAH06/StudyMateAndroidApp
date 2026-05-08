package com.example.studymateandroidapp.feature.calendar.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.core.model.Exam
import com.example.studymateandroidapp.core.model.Task
//import com.example.studymateandroidapp.feature.exam.data.ExamRepository
//import com.example.studymateandroidapp.feature.task.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * ViewModel for the Calendar screen.
 *
 * Manages the current month view, date selection, and
 * groups tasks/exams by date for markers and agenda.
 */
class CalendarViewModel(
//    private val taskRepository: TaskRepository,
//    private val examRepository: ExamRepository
) : ViewModel() {

    data class CalendarUiState(
        val currentMonth: YearMonth = YearMonth.now(),
        val selectedDate: LocalDate = LocalDate.now(),
        val eventsByDate: Map<LocalDate, List<CalendarEvent>> = emptyMap(),
        val isLoading: Boolean = true
    )

    sealed class CalendarEvent {
        data class TaskEvent(val task: Task) : CalendarEvent()
        data class ExamEvent(val exam: Exam) : CalendarEvent()

        val date: LocalDate get() = when(this) {
            is TaskEvent -> task.dueDate ?: LocalDate.now()
            is ExamEvent -> java.time.Instant.ofEpochMilli(exam.examDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        }
    }

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
//            combine(
//                taskRepository.getAllTasks(),
//                examRepository.getAllExams()
//            ) { task, exams ->
//                val allEvents = tasks.map { CalendarEvent.TaskEvent(it) } +
//                        exams.map { CalendarEvent.ExamEvent(it) }
//
//                allEvents.groupBy { it.date }
//            }.collect { eventsMap ->
//                _uiState.update { it.copy(eventsByDate = eventsMap, isLoading = false) }
//            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onMonthChanged(newMonth: YearMonth) {
        _uiState.update { it.copy(currentMonth = newMonth) }
    }

    fun onGoToToday() {
        _uiState.update {
            it.copy(
                currentMonth = YearMonth.now(),
                selectedDate = LocalDate.now()
            )
        }
    }
}
