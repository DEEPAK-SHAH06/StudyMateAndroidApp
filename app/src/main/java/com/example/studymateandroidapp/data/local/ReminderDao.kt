package com.example.studymateandroidapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.model.ReminderType
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_settings")
    fun getAllSettings(): Flow<List<ReminderSetting>>

    @Query("SELECT * FROM reminder_settings WHERE type = :type")
    suspend fun getSettingByType(type: ReminderType): ReminderSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSetting(setting: ReminderSetting)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultSettings(settings: List<ReminderSetting>)

    @Query("UPDATE reminder_settings SET isEnabled = :isEnabled WHERE type = :type")
    suspend fun updateEnabledStatus(type: ReminderType, isEnabled: Boolean)

    @Query("UPDATE reminder_settings SET scheduledTime = :time WHERE type = :type")
    suspend fun updateScheduledTime(type: ReminderType, time: java.time.LocalTime)

    @Query("SELECT * FROM reminder_settings")
    suspend fun getAllSettingsList(): List<ReminderSetting>
}
