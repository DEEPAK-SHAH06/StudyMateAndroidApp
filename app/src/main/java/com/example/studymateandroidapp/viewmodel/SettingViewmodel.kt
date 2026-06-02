package com.example.studymateandroidapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.repository.AuthRepository
import com.example.studymateandroidapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewmodel(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val settings: StateFlow<List<ReminderSetting>> = notificationRepository.allSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val currentUser = authRepository.currentUser

    val syncStatus: StateFlow<String> = preferenceManager.syncStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "IDLE"
        )

    val lastSyncTime: StateFlow<Long> = preferenceManager.lastSyncTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0L
        )

    init {
        viewModelScope.launch {
            try {
                notificationRepository.initializeDefaults()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize reminder defaults", e)
            }
        }
    }

    fun toggleReminder(setting: ReminderSetting) {
        Log.d(TAG, "Toggle received: type=${setting.type}, enabled=${setting.isEnabled}")
        viewModelScope.launch {
            try {
                notificationRepository.updateSetting(setting)
            } catch (e: Exception) {
                Log.e(TAG, "Toggle failed: type=${setting.type}", e)
            }
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

    companion object {
        private const val TAG = "SettingViewmodel"
    }
}
