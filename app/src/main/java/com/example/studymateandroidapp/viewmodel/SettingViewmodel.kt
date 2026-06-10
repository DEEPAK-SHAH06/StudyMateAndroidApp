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
import java.io.File
import java.io.FileOutputStream

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

    val themeMode: StateFlow<Int> = preferenceManager.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    val isAppLockEnabled: StateFlow<Boolean> = preferenceManager.isAppLockEnabled
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
        observeLocalProfile()
        syncWithFirebase()
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            preferenceManager.setThemeMode(mode)
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setAppLockEnabled(enabled)
        }
    }

    private fun observeLocalProfile() {
        viewModelScope.launch {
            preferenceManager.userName.collect { name ->
                _profileState.value = _profileState.value.copy(currentUsername = name)
            }
        }
        viewModelScope.launch {
            preferenceManager.userPhotoUri.collect { uri ->
                _profileState.value = _profileState.value.copy(photoUrl = uri)
            }
        }
    }

    private fun syncWithFirebase() {
        val user = ProfileRepository.getCurrentUser()
        if (user != null) {
            viewModelScope.launch {
                // If local is default or empty, sync from Firebase
                val currentLocalName = _profileState.value.currentUsername
                if (currentLocalName == "Anastasia" || currentLocalName.isEmpty()) {
                    user.displayName?.let { preferenceManager.setUserName(it) }
                }
                val currentLocalPhoto = _profileState.value.photoUrl
                if (currentLocalPhoto == null) {
                    user.photoUrl?.let { preferenceManager.setUserPhotoUri(it.toString()) }
                }
            }
        }
    }

    fun setSelectedImageUri(uri: Uri) {
        _profileState.value = _profileState.value.copy(selectedImageUri = uri)
    }

    private fun saveImageLocally(uri: Uri): Uri? {
        return try {
            val inputStream = authRepository.context.contentResolver.openInputStream(uri)
            val file = File(authRepository.context.filesDir, "profile_picture.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image locally", e)
            null
        }
    }

    fun updateProfile(username: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                // 1. Save locally first (Offline-first)
                preferenceManager.setUserName(username)
                
                var localUri: Uri? = null
                if (imageUri != null) {
                    localUri = saveImageLocally(imageUri)
                    if (localUri != null) {
                        preferenceManager.setUserPhotoUri(localUri.toString())
                    }
                }

                // 2. Try to sync with Firebase if online and logged in
                if (authRepository.isNetworkAvailable() && authRepository.getUserId() != null) {
                    val result = ProfileRepository.updateProfile(username, imageUri)
                    if (result.isSuccess) {
                        val user = ProfileRepository.getCurrentUser()
                        _profileState.value = _profileState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            currentUsername = user?.displayName ?: username,
                            photoUrl = user?.photoUrl?.toString() ?: localUri?.toString(),
                            selectedImageUri = null
                        )
                    } else {
                        // We still consider it a partial success because it's saved locally
                        _profileState.value = _profileState.value.copy(
                            isLoading = false,
                            isSuccess = true, // Still success locally
                            selectedImageUri = null
                        )
                    }
                } else {
                    // Offline success
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        selectedImageUri = null
                    )
                }
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Something went wrong"
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

    fun signInWithGoogle(activity: android.app.Activity) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(activity)

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