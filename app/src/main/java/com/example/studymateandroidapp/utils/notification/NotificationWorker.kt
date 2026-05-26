package com.example.studymateandroidapp.utils.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.model.ReminderType
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

/**
 * Unified WorkManager worker for periodic notification checks.
 *
 * Handles:
 * 1. **Daily Study Reminder**: Habit building alert.
 * 2. **Missed Task Alert**: Check for incomplete tasks due today.
 * 3. **Daily Goal Alert**: Check if study goals are met.
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = StudyPlannerDatabase.getInstance(applicationContext)
            val reminderDao = database.reminderDao()
            val manager = applicationContext.getSystemService<NotificationManager>() ?: return Result.failure()

            // Focus Mode check
            val focusSetting = reminderDao.getSettingByType(ReminderType.FOCUS_MODE)
            if (focusSetting?.isEnabled == true) return Result.success() // Skip alerts during focus

            // 1. Daily Habit Reminder
            val habitSetting = reminderDao.getSettingByType(ReminderType.DAILY_HABIT)
            if (habitSetting?.isEnabled == true) {
//                val sessionDao = database.sessionDao()
                val today = LocalDate.now()
//                val todayMinutes = sessionDao.getStudyMinutesForDay(
//                    today.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
//                ).first()
//                val notification = NotificationHelper.buildDailyReminder(applicationContext, todayMinutes).build()
//                manager.notify(NotificationHelper.DAILY_REMINDER_ID, notification)
            }

            // 2. Missed Task Reminder
            val missedTaskSetting = reminderDao.getSettingByType(ReminderType.MISSED_TASK)
            if (missedTaskSetting?.isEnabled == true) {
//                val taskDao = database.taskDao()
//                val incompleteTasksToday = taskDao.getIncompleteTasksDueOn(LocalDate.now()).first()
//                if (incompleteTasksToday.isNotEmpty()) {
//                    val notification = NotificationHelper.buildMissedTaskAlert(applicationContext, incompleteTasksToday.size).build()
//                    manager.notify(NotificationHelper.MISSED_TASK_ID, notification)
//                }
            }

            // 3. Daily Goal Reminder
            val goalSetting = reminderDao.getSettingByType(ReminderType.DAILY_GOAL)
            if (goalSetting?.isEnabled == true) {
//                val goalDao = database.goalDao()
//                val activeGoals = goalDao.getAllGoals().first()
                // Simple logic: if any goal due today or daily habit goal not met
                // For now, let's just check if there are goals with currentValue < targetValue
//                val unmetGoals = activeGoals.filter { it.deadline == LocalDate.now() && it.currentValue < it.targetValue }
//                if (unmetGoals.isNotEmpty()) {
//                    val notification = NotificationHelper.buildDailyGoalAlert(applicationContext, unmetGoals.first().targetValue - unmetGoals.first().currentValue).build()
//                    manager.notify(NotificationHelper.DAILY_GOAL_ID, notification)
//                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "study_planner_notifications"

        fun enqueue(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
        }
    }
}
