package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.StudySession
import com.example.studymateandroidapp.data.repository.SessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Manages the Pomodoro timer and stopwatch logic.
 */
class TimerViewmodel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    enum class TimerMode { POMODORO, STOPWATCH }
    enum class PomodoroPhase { WORK, BREAK, LONG_BREAK }

    data class TimerUiState(
        val mode: TimerMode = TimerMode.POMODORO,
        val phase: PomodoroPhase = PomodoroPhase.WORK,
        val timeLeftSeconds: Int = 25 * 60,
        val isRunning: Boolean = false,
        val totalSecondsWorkedToday: Int = 0,
        val studyTitle: String = "",
        val recentSessions: List<StudySession> = emptyList()
    )

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: LocalDateTime? = null

    init {
        loadRecentSessions()
    }

    private fun loadRecentSessions() {
        viewModelScope.launch {
            sessionRepository.getAllSessions().collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions.take(10)) }
                val todaySeconds = sessions.filter { 
                    it.startTime.toLocalDate() == java.time.LocalDate.now() 
                }.sumOf { it.durationMinutes * 60 }
                _uiState.update { it.copy(totalSecondsWorkedToday = todaySeconds) }
            }
        }
    }

    fun setMode(mode: TimerMode) {
        stopTimer()
        _uiState.update { 
            it.copy(
                mode = mode, 
                timeLeftSeconds = if (mode == TimerMode.POMODORO) getPhaseDuration(it.phase) else 0 
            ) 
        }
    }

    fun setPhase(phase: PomodoroPhase) {
        stopTimer()
        _uiState.update { it.copy(phase = phase, timeLeftSeconds = getPhaseDuration(phase)) }
    }

    fun updateStudyTitle(title: String) {
        _uiState.update { it.copy(studyTitle = title) }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return
        _uiState.update { it.copy(isRunning = true) }
        startTime = LocalDateTime.now()

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    if (state.mode == TimerMode.POMODORO) {
                        if (state.timeLeftSeconds > 0) {
                            state.copy(timeLeftSeconds = state.timeLeftSeconds - 1)
                        } else {
                            onTimerFinished()
                            state.copy(isRunning = false)
                        }
                    } else {
                        state.copy(timeLeftSeconds = state.timeLeftSeconds + 1)
                    }
                }
                if (!_uiState.value.isRunning) break
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
        
        if (_uiState.value.mode == TimerMode.STOPWATCH && _uiState.value.timeLeftSeconds > 0) {
            saveSession()
        }
    }

    fun resetTimer() {
        stopTimer()
        _uiState.update { 
            it.copy(
                timeLeftSeconds = if (it.mode == TimerMode.POMODORO) getPhaseDuration(it.phase) else 0 
            ) 
        }
    }

    private fun onTimerFinished() {
        if (_uiState.value.phase == PomodoroPhase.WORK) {
            saveSession()
        }
        // Logic for auto-switching phases could go here
    }

    private fun saveSession() {
        val durationSeconds = if (_uiState.value.mode == TimerMode.POMODORO) {
            getPhaseDuration(_uiState.value.phase) - _uiState.value.timeLeftSeconds
        } else {
            _uiState.value.timeLeftSeconds
        }

        if (durationSeconds < 10) return // Don't save very short sessions

        val session = StudySession(
            subject = _uiState.value.studyTitle.ifBlank { "Study Session" },
            startTime = startTime ?: LocalDateTime.now().minusSeconds(durationSeconds.toLong()),
            endTime = LocalDateTime.now(),
            durationMinutes = (durationSeconds / 60).coerceAtLeast(1),
            examId = null // Could be passed from screen
        )

        viewModelScope.launch {
            sessionRepository.insert(session)
            resetTimer()
        }
    }

    private fun getPhaseDuration(phase: PomodoroPhase): Int {
        return when (phase) {
            PomodoroPhase.WORK -> 25 * 60
            PomodoroPhase.BREAK -> 5 * 60
            PomodoroPhase.LONG_BREAK -> 15 * 60
        }
    }
}
