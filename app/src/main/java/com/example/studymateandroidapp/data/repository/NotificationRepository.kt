package com.example.studymateandroidapp.data.repository

import android.content.Context
import com.example.studymateandroidapp.data.local.ReminderDao
import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.local.ExamDao
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.model.ReminderType
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime

class NotificationRepository(
    private val context: Context,
    private val reminderDao: ReminderDao,
    private val taskDao: TaskDao,
    private val examDao: ExamDao,
    private val scheduler: ReminderScheduler
) {

    val allSettings: Flow<List<ReminderSetting>> = reminderDao.getAllSettings()

    suspend fun isFocusModeEnabled(): Boolean {
        val settings = reminderDao.getAllSettings().first()
        return settings.find { it.type == ReminderType.FOCUS_MODE }?.isEnabled ?: false
    }

    suspend fun updateSetting(setting: ReminderSetting) {
        reminderDao.upsertSetting(setting)
        rescheduleAll() // Simple but effective: refresh all alarms/workers
    }

    suspend fun rescheduleAll() {
        val settings = reminderDao.getAllSettings().first()
        
        // 1. Exact Alarms (Tasks & Exams)
        val taskSetting = settings.find { it.type == ReminderType.TASK }
        if (taskSetting?.isEnabled == true) {
            val pendingTasks = taskDao.getPendingTasks().first()
            pendingTasks.forEach { task ->
                task.dueDate?.let { date ->
                    scheduler.scheduleTaskReminder(task.id, task.title, date, taskSetting.scheduledTime ?: LocalTime.of(9, 0))
                }
            }
        } else {
            // Cancel all task alarms? (Would require knowing IDs, or just cancel when disabled)
        }

        val examSetting = settings.find { it.type == ReminderType.EXAM }
        if (examSetting?.isEnabled == true) {
            val exams = examDao.getAllExams().first()
            val daysBefore = listOf(1, 3, 7) // Or configurable
            exams.forEach { exam ->
                val localDate = java.time.Instant.ofEpochMilli(exam.examDate)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                scheduler.scheduleExamReminders(exam.id, exam.title, localDate, daysBefore)
            }
        }

        // 2. Periodic Workers (Habits, Missed Tasks, Goals)
//        val dailyHabit = settings.find { it.type == ReminderType.DAILY_HABIT }
//        val missedTask = settings.find { it.type == ReminderType.MISSED_TASK }
//        val dailyGoal = settings.find { it.type == ReminderType.DAILY_GOAL }

//        if (dailyHabit?.isEnabled == true || missedTask?.isEnabled == true || dailyGoal?.isEnabled == true) {
//            NotificationWorker.enqueue(context)
//        } else {
//            NotificationWorker.cancel(context)
//        }
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
        reminderDao.insertDefaultSettings(defaults)
        rescheduleAll()
    }
}
