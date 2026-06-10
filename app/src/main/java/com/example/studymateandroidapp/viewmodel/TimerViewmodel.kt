package com.example.studymateandroidapp.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.data.model.StudySession
import com.example.studymateandroidapp.data.repository.SessionRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository
import com.example.studymateandroidapp.utils.notification.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Manages the Pomodoro timer and stopwatch logic with exact-second precision.
 */
class TimerViewmodel(
    private val sessionRepository: SessionRepository,
    private val motivationRepository: MotivationRepository,
    private val preferenceManager: PreferenceManager,
    private val context: Context
) : ViewModel() {

    enum class TimerMode { POMODORO, STOPWATCH }
    enum class PomodoroPhase { WORK, BREAK, LONG_BREAK }

    data class TimerUiState(
        val mode: TimerMode = TimerMode.POMODORO,
        val phase: PomodoroPhase = PomodoroPhase.WORK,
        val timeLeftSeconds: Int = 25 * 60,
        val isRunning: Boolean = false,
        val isPaused: Boolean = false,
        val totalSecondsWorkedToday: Int = 0,
        val studyTitle: String = "",
        val examId: Long? = null,
        val recentSessions: List<StudySession> = emptyList()
    )

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: LocalDateTime? = null
    private var accumulatedSeconds: Int = 0

    init {
        loadRecentSessions()
    }

    private fun loadRecentSessions() {
        viewModelScope.launch {
            sessionRepository.getAllSessions().collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions.take(10)) }
                val todaySeconds = sessions.filter { 
                    it.startTime.toLocalDate() == java.time.LocalDate.now() 
                }.sumOf { it.durationSeconds }
                _uiState.update { it.copy(totalSecondsWorkedToday = todaySeconds) }
            }
        }
    }

    fun setExamId(id: Long?) {
        _uiState.update { it.copy(examId = id) }
    }

    fun setMode(mode: TimerMode) {
        if (_uiState.value.isRunning || _uiState.value.isPaused) return
        _uiState.update { 
            it.copy(
                mode = mode, 
                timeLeftSeconds = if (mode == TimerMode.POMODORO) getPhaseDuration(it.phase) else 0 
            ) 
        }
    }

    fun setPhase(phase: PomodoroPhase) {
        if (_uiState.value.isRunning || _uiState.value.isPaused) return
        _uiState.update { it.copy(phase = phase, timeLeftSeconds = getPhaseDuration(phase)) }
    }

    fun updateStudyTitle(title: String) {
        _uiState.update { it.copy(studyTitle = title) }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return
        
        _uiState.update { it.copy(isRunning = true, isPaused = false) }
        viewModelScope.launch { preferenceManager.setTimerRunning(true) }
        
        if (startTime == null) {
            startTime = LocalDateTime.now()
        }

        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(1000)
                _uiState.update { state ->
                    if (state.mode == TimerMode.POMODORO) {
                        if (state.timeLeftSeconds > 0) {
                            state.copy(timeLeftSeconds = state.timeLeftSeconds - 1)
                        } else {
                            state.copy(isRunning = false)
                        }
                    } else {
                        state.copy(timeLeftSeconds = state.timeLeftSeconds + 1)
                    }
                }
                
                if (_uiState.value.mode == TimerMode.POMODORO && _uiState.value.timeLeftSeconds == 0) {
                    onTimerFinished()
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        if (!_uiState.value.isRunning) return
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = true) }
        viewModelScope.launch { preferenceManager.setTimerRunning(false) }
    }

    fun resumeTimer() {
        if (!_uiState.value.isPaused) return
        startTimer()
    }

    fun stopAndSave() {
        val wasRunningOrPaused = _uiState.value.isRunning || _uiState.value.isPaused
        if (!wasRunningOrPaused) return

        timerJob?.cancel()
        val durationSeconds = if (_uiState.value.mode == TimerMode.POMODORO) {
            getPhaseDuration(_uiState.value.phase) - _uiState.value.timeLeftSeconds
        } else {
            _uiState.value.timeLeftSeconds
        }

        if (durationSeconds >= 1) {
            // Only save study sessions for WORK phase in Pomodoro or always for Stopwatch
            if (_uiState.value.mode == TimerMode.STOPWATCH || _uiState.value.phase == PomodoroPhase.WORK) {
                saveSession(durationSeconds, isCompleted = false)
            }
        }

        resetTimerInternal()
    }

    fun resetTimer() {
        timerJob?.cancel()
        resetTimerInternal()
    }

    private fun resetTimerInternal() {
        _uiState.update { 
            it.copy(
                isRunning = false,
                isPaused = false,
                timeLeftSeconds = if (it.mode == TimerMode.POMODORO) getPhaseDuration(it.phase) else 0 
            ) 
        }
        viewModelScope.launch { preferenceManager.setTimerRunning(false) }
        startTime = null
    }

    private fun onTimerFinished() {
        val durationSeconds = getPhaseDuration(_uiState.value.phase)
        
        if (_uiState.value.phase == PomodoroPhase.WORK) {
            saveSession(durationSeconds, isCompleted = true)
        }
        
        triggerCompletionAlert()
        resetTimerInternal()
    }

    private fun saveSession(seconds: Int, isCompleted: Boolean) {
        val session = StudySession(
            subject = _uiState.value.studyTitle.ifBlank { "Study Session" },
            startTime = startTime ?: LocalDateTime.now().minusSeconds(seconds.toLong()),
            endTime = LocalDateTime.now(),
            durationSeconds = seconds,
            isCompleted = isCompleted,
            examId = _uiState.value.examId
        )

        viewModelScope.launch {
            sessionRepository.insert(session)
            motivationRepository.checkAndUnlockAchievements()
        }
    }

    private fun triggerCompletionAlert() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val title = when(_uiState.value.phase) {
            PomodoroPhase.WORK -> "Work Session Complete!"
            else -> "Break is Over!"
        }
        val message = when(_uiState.value.phase) {
            PomodoroPhase.WORK -> "Great job! Time for a well-deserved break."
            else -> "Ready to get back to work?"
        }

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_TASK_REMINDER) // Reuse high priority
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .build()

        manager.notify(9999, notification)
    }

    private fun getPhaseDuration(phase: PomodoroPhase): Int {
        return when (phase) {
            PomodoroPhase.WORK -> 25 * 60
            PomodoroPhase.BREAK -> 5 * 60
            PomodoroPhase.LONG_BREAK -> 15 * 60
        }
    }
}
