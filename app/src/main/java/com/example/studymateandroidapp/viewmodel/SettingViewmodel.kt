package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.repository.AuthRepository
import com.example.studymateandroidapp.data.repository.NotificationRepository
import com.example.studymateandroidapp.data.local.PreferenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for SettingsScreen.
 *
 * - Notification toggles → persisted via NotificationRepository (Room)
 * - Auth state           → AuthRepository (Firebase)
 * - Sync status          → PreferenceManager (DataStore)
 */
class SettingViewmodel(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    /** Live list of all reminder toggles (Task, Exam, Daily habit, etc.) */
    val settings: StateFlow<List<ReminderSetting>> = notificationRepository.allSettings
        .stateIn(
            scope          = viewModelScope,
            started        = SharingStarted.WhileSubscribed(5_000),
            initialValue   = emptyList()
        )

    /** Currently signed-in Firebase user, or null if guest. */
    val currentUser = authRepository.currentUser

    /** "IDLE" | "SYNCING" | "SUCCESS" | "ERROR" */
    val syncStatus: StateFlow<String> = preferenceManager.syncStatus
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = "IDLE"
        )

    val lastSyncTime: StateFlow<Long> = preferenceManager.lastSyncTime
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0L
        )

    // ── Events ────────────────────────────────────────────
    fun toggleReminder(setting: ReminderSetting) {
        viewModelScope.launch {
            notificationRepository.updateSetting(setting)
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            authRepository.signInWithGoogle()
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun getUserId(): String? = authRepository.getUserId()
}