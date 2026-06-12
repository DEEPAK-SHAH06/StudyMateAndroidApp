package com.example.studymateandroidapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manages app-wide persistent preferences (Theme, etc.) using Jetpack DataStore.
 */
class PreferenceManager(private val context: Context) {

    companion object {
        val THEME_MODE = intPreferencesKey("theme_mode") // 0: System, 1: Light, 2: Dark
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val SYNC_STATUS = stringPreferencesKey("sync_status") // "IDLE", "SYNCING", "ERROR"
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHOTO_URI = stringPreferencesKey("user_photo_uri")
        val USER_BIO = stringPreferencesKey("user_bio")
        val IS_SYNC_ENABLED = booleanPreferencesKey("is_sync_enabled")
        val IS_TIMER_RUNNING = booleanPreferencesKey("is_timer_running")
        val LAST_DAILY_HABIT_REMINDER_DATE = stringPreferencesKey("last_daily_habit_reminder_date")
    }

    val themeMode: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE] ?: 0
        }

    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[APP_LOCK_ENABLED] ?: false
        }

    val syncStatus: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SYNC_STATUS] ?: "IDLE"
        }

    val lastSyncTime: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SYNC_TIME] ?: 0L
        }

    val userName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME] ?: "Guest"
        }

    val userPhotoUri: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_PHOTO_URI]
        }

    val userBio: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_BIO] ?: "Ready for your deep work session?"
        }

    val isSyncEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_SYNC_ENABLED] ?: false
        }

    val isTimerRunning: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_TIMER_RUNNING] ?: false
        }

    val lastDailyHabitReminderDate: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_DAILY_HABIT_REMINDER_DATE]
        }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[APP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setSyncState(status: String, timestamp: Long? = null) {
        context.dataStore.edit { preferences ->
            preferences[SYNC_STATUS] = status
            timestamp?.let { preferences[LAST_SYNC_TIME] = it }
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun setUserPhotoUri(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[USER_PHOTO_URI] = uri
            } else {
                preferences.remove(USER_PHOTO_URI)
            }
        }
    }

    suspend fun setUserBio(bio: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_BIO] = bio
        }
    }

    suspend fun setSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SYNC_ENABLED] = enabled
        }
    }

    suspend fun setTimerRunning(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_TIMER_RUNNING] = enabled
        }
    }

    suspend fun setLastDailyHabitReminderDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_DAILY_HABIT_REMINDER_DATE] = date
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
