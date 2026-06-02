package com.example.studymateandroidapp.data.repository

import android.content.Context
import android.app.NotificationManager
import android.util.Log
import com.example.studymateandroidapp.data.local.ReminderDao
import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.local.ExamDao
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.model.ReminderType
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import com.example.studymateandroidapp.utils.notification.NotificationHelper
import com.example.studymateandroidapp.utils.notification.NotificationWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneId

class NotificationRepository(
    private val context: Context,
    private val reminderDao: ReminderDao,
    private val taskDao: TaskDao,
    private val examDao: ExamDao,
    private val scheduler: ReminderScheduler
) {

    val allSettings: Flow<List<ReminderSetting>> = reminderDao.getAllSettings()
        .onEach { settings ->
            Log.d(TAG, "allSettings emitted ${settings.size} reminder settings")
        }

    suspend fun isFocusModeEnabled(): Boolean {
        val settings = reminderDao.getAllSettings().first()
        return settings.find { it.type == ReminderType.FOCUS_MODE }?.isEnabled ?: false
    }

    suspend fun updateSetting(setting: ReminderSetting) {
        Log.d(TAG, "updateSetting requested: type=${setting.type}, enabled=${setting.isEnabled}")
        try {
            val updatedSetting = setting.copy(lastUpdated = System.currentTimeMillis())
            reminderDao.upsertSetting(updatedSetting)
            Log.d(TAG, "Room upsert complete: type=${updatedSetting.type}, enabled=${updatedSetting.isEnabled}")

            if (updatedSetting.isEnabled) {
                scheduleReminderType(updatedSetting)
            } else {
                cancelReminderType(updatedSetting.type)
            }
            updatePeriodicWorker()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update reminder setting: type=${setting.type}", e)
            throw e
        }
    }

    suspend fun rescheduleAll() {
        val settings = reminderDao.getAllSettings().first()

        settings.forEach { setting ->
            if (setting.isEnabled) {
                scheduleReminderType(setting)
            } else {
                cancelReminderType(setting.type)
            }
        }
        updatePeriodicWorker(settings)
    }

    suspend fun initializeDefaults() {
        val defaults = listOf(
            ReminderSetting(ReminderType.TASK, isEnabled = true, scheduledTime = LocalTime.of(9, 0)),
            ReminderSetting(ReminderType.EXAM, isEnabled = true, daysBefore = 1),
            ReminderSetting(ReminderType.DAILY_HABIT, isEnabled = true, scheduledTime = LocalTime.of(8, 0)),
            ReminderSetting(ReminderType.MISSED_TASK, isEnabled = true, scheduledTime = LocalTime.of(21, 0)),
            ReminderSetting(ReminderType.DAILY_GOAL, isEnabled = true, scheduledTime = LocalTime.of(20, 0)),
            ReminderSetting(ReminderType.FOCUS_MODE, isEnabled = false)
        )
        val beforeCount = reminderDao.getAllSettings().first().size
        Log.d(TAG, "initializeDefaults called; existing settings count=$beforeCount")
        reminderDao.insertDefaultSettings(defaults)
        val settings = reminderDao.getAllSettings().first()
        Log.d(TAG, "initializeDefaults complete; settings count=${settings.size}")
        rescheduleAll()
    }

    private suspend fun scheduleReminderType(setting: ReminderSetting) {
        when (setting.type) {
            ReminderType.TASK -> scheduleTaskReminders(setting)
            ReminderType.EXAM -> scheduleExamReminders(setting)
            ReminderType.DAILY_HABIT,
            ReminderType.MISSED_TASK,
            ReminderType.DAILY_GOAL,
            ReminderType.FOCUS_MODE -> Unit
        }
    }

    private suspend fun cancelReminderType(type: ReminderType) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (type) {
            ReminderType.TASK -> {
                taskDao.getAllTasksList().forEach { task ->
                    scheduler.cancelTaskReminder(task.id)
                    notificationManager.cancel(NotificationHelper.TASK_NOTIFICATION_BASE + task.id.toInt())
                }
            }
            ReminderType.EXAM -> {
                examDao.getAllExams().first().forEach { exam ->
                    scheduler.cancelExamReminders(exam.id)
                    notificationManager.cancel(NotificationHelper.EXAM_NOTIFICATION_BASE + exam.id.toInt())
                }
            }
            ReminderType.DAILY_HABIT ->
                notificationManager.cancel(NotificationHelper.DAILY_REMINDER_ID)
            ReminderType.MISSED_TASK ->
                notificationManager.cancel(NotificationHelper.MISSED_TASK_ID)
            ReminderType.DAILY_GOAL ->
                notificationManager.cancel(NotificationHelper.DAILY_GOAL_ID)
            ReminderType.FOCUS_MODE -> Unit
        }
    }

    private suspend fun scheduleTaskReminders(setting: ReminderSetting) {
        val reminderTime = setting.scheduledTime ?: LocalTime.of(9, 0)
        taskDao.getPendingTasks().first().forEach { task ->
            task.dueDate?.let { dueDate ->
                scheduler.scheduleTaskReminder(task.id, task.title, dueDate, reminderTime)
            }
        }
    }

    private suspend fun scheduleExamReminders(setting: ReminderSetting) {
        val configuredDays = setting.daysBefore
        val daysBefore = listOf(configuredDays ?: 1, 3).distinct()
        examDao.getAllExams().first().forEach { exam ->
            val examDate = Instant.ofEpochMilli(exam.examDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            scheduler.scheduleExamReminders(exam.id, exam.title, examDate, daysBefore)
        }
    }

    private suspend fun updatePeriodicWorker(settings: List<ReminderSetting>? = null) {
        val currentSettings = settings ?: reminderDao.getAllSettings().first()
        val periodicTypes = setOf(
            ReminderType.DAILY_HABIT,
            ReminderType.MISSED_TASK,
            ReminderType.DAILY_GOAL
        )
        if (currentSettings.any { it.type in periodicTypes && it.isEnabled }) {
            Log.d(TAG, "Periodic notification worker enabled")
            NotificationWorker.enqueue(context)
        } else {
            Log.d(TAG, "Periodic notification worker disabled")
            NotificationWorker.cancel(context)
        }
    }

    companion object {
        private const val TAG = "NotificationRepository"
    }
}
