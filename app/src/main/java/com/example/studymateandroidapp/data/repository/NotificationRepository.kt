package com.example.studymateandroidapp.data.repository

import android.content.Context
import android.app.NotificationManager
import android.util.Log
import com.example.studymateandroidapp.data.local.ReminderDao
import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.local.ExamDao
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.model.ReminderType
import com.example.studymateandroidapp.utils.notification.NotificationWorker
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import com.example.studymateandroidapp.utils.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDateTime

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

    suspend fun updateSetting(setting: ReminderSetting) {
        Log.d(TAG, "updateSetting requested: type=${setting.type}, enabled=${setting.isEnabled}")
        try {
            val updatedSetting = setting.copy(lastUpdated = System.currentTimeMillis())
            reminderDao.upsertSetting(updatedSetting)

            if (updatedSetting.isEnabled) {
                scheduleReminderType(updatedSetting)
            } else {
                cancelReminderType(updatedSetting.type)
            }
            updatePeriodicWorker()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update reminder setting", e)
        }
    }

    suspend fun rescheduleAll() {
        Log.d(TAG, "Rescheduling all enabled reminders")
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
            ReminderSetting(ReminderType.TASK, isEnabled = true),
            ReminderSetting(ReminderType.EXAM, isEnabled = true),
            ReminderSetting(ReminderType.DAILY_HABIT, isEnabled = true),
            ReminderSetting(ReminderType.MISSED_TASK, isEnabled = true),
            ReminderSetting(ReminderType.DAILY_GOAL, isEnabled = true)
        )
        reminderDao.insertDefaultSettings(defaults)
        rescheduleAll()
    }

    private suspend fun scheduleReminderType(setting: ReminderSetting) {
        when (setting.type) {
            ReminderType.TASK -> scheduleTaskReminders()
            ReminderType.EXAM -> scheduleExamReminders()
            else -> { /* Handled by WorkManager */ }
        }
    }

    private suspend fun cancelReminderType(type: ReminderType) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        when (type) {
            ReminderType.TASK -> {
                taskDao.getAllTasksList().forEach { 
                    scheduler.cancelTaskReminders(it.id)
                    manager.cancel(NotificationHelper.TASK_NOTIFICATION_BASE + it.id.toInt())
                }
            }
            ReminderType.EXAM -> {
                examDao.getAllExams().first().forEach { 
                    scheduler.cancelExamReminders(it.id)
                    manager.cancel(NotificationHelper.EXAM_NOTIFICATION_BASE + it.id.toInt())
                }
            }
            ReminderType.DAILY_HABIT -> manager.cancel(NotificationHelper.DAILY_REMINDER_ID)
            ReminderType.MISSED_TASK -> manager.cancel(NotificationHelper.MISSED_TASK_ID)
            ReminderType.DAILY_GOAL -> manager.cancel(NotificationHelper.DAILY_GOAL_ID)
        }
    }

    private suspend fun scheduleTaskReminders() {
        taskDao.getPendingTasks().first().forEach { task ->
            if (task.dueDate != null && task.dueTime != null) {
                scheduler.scheduleTaskReminders(task.id, task.title, task.dueDate, task.dueTime)
            }
        }
    }

    private suspend fun scheduleExamReminders() {
        examDao.getAllExams().first().forEach { exam ->
            val ldt = Instant.ofEpochMilli(exam.examDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
            scheduler.scheduleExamReminders(exam.id, exam.title, exam.subject, ldt, exam.isTimeSet)
        }
    }

    private suspend fun updatePeriodicWorker(settings: List<ReminderSetting>? = null) {
        val currentSettings = settings ?: reminderDao.getAllSettings().first()
        // Always enqueue for windows (Morning/EOD) if app is active
        NotificationWorker.enqueue(context)
        Log.d(TAG, "Periodic worker enqueued/updated")
    }

    companion object {
        private const val TAG = "NotificationRepository"
    }
}
