package com.example.studymateandroidapp.data.local

import androidx.room.TypeConverter
import com.example.studymateandroidapp.data.model.AchievementType
import com.example.studymateandroidapp.data.model.GoalStatus
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.data.model.ReminderType
import com.example.studymateandroidapp.data.model.TaskStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Room [TypeConverter]s for types that Room cannot persist natively.
 *
 * - [LocalDate] ↔ epoch day (`Long`)
 * - [LocalDateTime] ↔ epoch second (`Long`)
 * - Enums ↔ name `String` (safe against ordinal reordering)
 */
class Converters {

    // ── LocalDate ↔ Long (epoch day) ──────────────────────
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? =
        epochDay?.let { LocalDate.ofEpochDay(it) }


    // ── LocalDateTime ↔ Long (epoch second) ───────────────
    @TypeConverter
    fun fromLocalDateTime(dt: LocalDateTime?): Long? =
        dt?.atZone(ZoneId.systemDefault())?.toEpochSecond()

    @TypeConverter
    fun toLocalDateTime(epoch: Long?): LocalDateTime? =
        epoch?.let {
            LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault())
        }


    // ── Enum ↔ String ─────────────────────────────────────
    @TypeConverter fun fromPriority(value: Priority): String = value.name
    @TypeConverter fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter fun fromTaskStatus(value: TaskStatus): String = value.name
    @TypeConverter fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter fun fromGoalStatus(value: GoalStatus): String = value.name
    @TypeConverter fun toGoalStatus(value: String): GoalStatus = GoalStatus.valueOf(value)

    @TypeConverter fun fromAchievementType(value: AchievementType): String = value.name
    @TypeConverter fun toAchievementType(value: String): AchievementType = AchievementType.valueOf(value)

    @TypeConverter fun fromReminderType(value: ReminderType): String = value.name
    @TypeConverter fun toReminderType(value: String): ReminderType {
        return try {
            ReminderType.valueOf(value)
        } catch (e: Exception) {
            android.util.Log.e("Converters", "Unknown ReminderType: $value, falling back to TASK")
            ReminderType.TASK
        }
    }


    // ── LocalTime ↔ Long (nano-of-day) ───────────────────
    @TypeConverter
    fun fromLocalTime(time: java.time.LocalTime?): Long? = time?.toNanoOfDay()

    @TypeConverter
    fun toLocalTime(nano: Long?): java.time.LocalTime? =
        nano?.let { java.time.LocalTime.ofNanoOfDay(it) }


    // ── List<String> ↔ String (comma-separated) ───────────
    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toStringList(data: String?): List<String>? = 
        if (data.isNullOrEmpty()) emptyList() else data.split(",")
}
