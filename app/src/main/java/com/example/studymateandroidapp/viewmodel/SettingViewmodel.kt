package com.example.studymateandroidapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.data.model.ProfileState
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.repository.AuthRepository
import com.example.studymateandroidapp.data.repository.NotificationRepository
import com.example.studymateandroidapp.data.repository.ProfileRepository
import com.example.studymateandroidapp.utils.sync.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewmodel(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState

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

    val isSyncEnabled: StateFlow<Boolean> = preferenceManager.isSyncEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError = _uiError.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                notificationRepository.initializeDefaults()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize reminder defaults", e)
            }
        }
        loadCurrentUser()
    }
    private fun loadCurrentUser() {
        val user = ProfileRepository.getCurrentUser()
        _profileState.value = ProfileState(
            currentUsername = user?.displayName ?: "",
            photoUrl = user?.photoUrl?.toString()
        )
    }

    fun setSelectedImageUri(uri: Uri) {
        _profileState.value = _profileState.value.copy(selectedImageUri = uri)
    }

    fun updateProfile(username: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = ProfileRepository.updateProfile(username, imageUri)

            if (result.isSuccess) {

                val user = ProfileRepository.getCurrentUser()

                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    currentUsername = user?.displayName ?: username,
                    photoUrl = user?.photoUrl?.toString(),
                    selectedImageUri = null
                )
            } else {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Something went wrong"
                )
            }
        }
    }

    fun resetProfileState() {
        _profileState.value = _profileState.value.copy(
            isSuccess = false,
            errorMessage = null
        )
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
            val result = authRepository.signInWithGoogle()

            if (result.isFailure) {
                _uiError.value =
                    result.exceptionOrNull()?.message
                        ?: "Unknown Error"
            }
        }
    }

    fun clearError() {
        _uiError.value = null
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun toggleSync(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setSyncEnabled(enabled)

            if (enabled) {
                SyncManager(authRepository.context)
                    .triggerImmediateSync()
            }
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            SyncManager(authRepository.context)
                .triggerImmediateSync()
        }
    }

    fun getUserId(): String? = authRepository.getUserId()

    companion object {
        private const val TAG = "SettingViewmodel"
    }
}