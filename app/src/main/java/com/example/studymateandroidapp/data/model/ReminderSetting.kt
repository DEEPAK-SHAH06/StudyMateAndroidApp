package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

/**
 * Represents a user-configurable reminder setting.
 *
 * Each [type] is unique, and it stores whether the reminder
 * is enabled and any specific configuration like time or days before.
 */
@Entity(tableName = "reminder_settings")
data class ReminderSetting(
    @PrimaryKey val type: ReminderType = ReminderType.TASK,
    val isEnabled: Boolean = true,
    val scheduledTime: LocalTime? = null,
    val daysBefore: Int? = null, // Used for exams (e.g. 1 day before, 3 days before)

    // Sync metadata
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class ReminderType {
    TASK,          // Before task deadline
    EXAM,          // Before exam (1-day, 3-day etc)
    DAILY_HABIT,   // General study habit reminder
    MISSED_TASK,   // If task incomplete at end of day
    DAILY_GOAL     // If goal not met at end of day
}
