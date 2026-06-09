package com.example.studymateandroidapp.utils.notification

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.data.model.GoalStatus
import com.example.studymateandroidapp.data.model.ReminderType
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

/**
 * Unified WorkManager worker for non-urgent periodic checks.
 *
 * Suppression Policy:
 * - If user is actively studying (IS_TIMER_RUNNING is true), 
 *   non-urgent notifications are SKIPPED to maintain focus.
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "NotificationWorker"

    override suspend fun doWork(): Result {
        Log.d(TAG, "NotificationWorker starting check")
        
        val preferenceManager = PreferenceManager(applicationContext)
        val isStudying = preferenceManager.isTimerRunning.first()
        val now = LocalTime.now()

        try {
            val database = StudyPlannerDatabase.getInstance(applicationContext)
            val manager = applicationContext.getSystemService<NotificationManager>() ?: return Result.failure()

            // ── 1. MORNING SUMMARY (8 AM Window) ──────────────────
            if (now.hour == 8) {
                Log.d(TAG, "Morning Summary Window Detected")
                val taskDao = database.taskDao()
                val tasksToday = taskDao.getIncompleteTasksDueOn(LocalDate.now()).first()
                if (tasksToday.isNotEmpty()) {
                    val notification = NotificationHelper.buildMorningSummary(applicationContext, tasksToday.size).build()
                    manager.notify(NotificationHelper.DAILY_REMINDER_ID, notification)
                    Log.d(TAG, "Posted Morning Summary")
                }
            }

            // ── 2. NON-URGENT (Suppressed if Studying) ──────────
            if (isStudying) {
                Log.d(TAG, "Non-urgent notifications SUPPRESSED: User is studying")
            } else {
                checkDailyHabit(database, manager, preferenceManager)
                checkDailyGoal(database, manager)
            }

            // ── 3. END OF DAY CHECK (8 PM Window) ─────────────────
            if (now.hour == 20) {
                Log.d(TAG, "End of Day Check Window Detected")
                val taskDao = database.taskDao()
                // Check for overdue unfinished tasks
                val overdueCount = taskDao.getOverdueCount(LocalDate.now(), LocalTime.now()).first()
                if (overdueCount > 0) {
                    val notification = NotificationHelper.buildEndOfDayCheck(applicationContext).build()
                    manager.notify(NotificationHelper.MISSED_TASK_ID, notification)
                    Log.d(TAG, "Posted End of Day Check")
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "NotificationWorker failed", e)
            return Result.retry()
        }
    }

    private suspend fun checkDailyHabit(
        db: StudyPlannerDatabase, 
        manager: NotificationManager,
        preferenceManager: PreferenceManager
    ) {
        val reminderDao = db.reminderDao()
        val setting = reminderDao.getSettingByType(ReminderType.DAILY_HABIT)
        if (setting?.isEnabled != true) {
            Log.d(TAG, "DailyStudy: Reminder is disabled -> skip")
            return
        }

        val now = LocalTime.now()
        val todayStr = LocalDate.now().toString()
        
        // Requirement: Default reminder time: 7:00 PM (19:00)
        if (now.hour < 19) {
            Log.d(TAG, "DailyStudy: Before 7:00 PM window -> skip")
            return
        }

        // Requirement: User has not already received a Daily Study Reminder today
        val lastReminderDate = preferenceManager.lastDailyHabitReminderDate.first()
        if (lastReminderDate == todayStr) {
            Log.d(TAG, "DailyStudy: Reminder already sent today ($todayStr) -> skip")
            return
        }

        // Requirement: User has not studied today
        val sessionDao = db.sessionDao()
        val today = LocalDate.now()
        val minutes = sessionDao.getStudyMinutesForDay(
            today.atStartOfDay(), 
            today.plusDays(1).atStartOfDay().minusNanos(1)
        ).first()

        if (minutes > 0) {
            Log.d(TAG, "DailyStudy: User studied today ($minutes min) -> skip")
            return
        }

        // Requirement: Reminder should NOT appear if active study session is in progress
        // (This is already handled by the isStudying check in doWork, but let's be safe)
        
        val notification = NotificationHelper.buildDailyReminder(applicationContext, minutes).build()
        manager.notify(NotificationHelper.DAILY_REMINDER_ID, notification)
        
        // Save the date to prevent duplicate notifications today
        preferenceManager.setLastDailyHabitReminderDate(todayStr)
        Log.d(TAG, "DailyStudy: Notification sent successfully")
    }

    private suspend fun checkDailyGoal(db: StudyPlannerDatabase, manager: NotificationManager) {
        val reminderDao = db.reminderDao()
        val setting = reminderDao.getSettingByType(ReminderType.DAILY_GOAL)
        if (setting?.isEnabled == true) {
            val goalDao = db.goalDao()
            val unmet = goalDao.getAllGoals().first().filter { it.status != GoalStatus.COMPLETED && it.deadline == LocalDate.now() }
            if (unmet.isNotEmpty()) {
                val remaining = (unmet.first().targetValue - unmet.first().currentValue).toInt()
                val notification = NotificationHelper.buildDailyGoalAlert(applicationContext, remaining).build()
                manager.notify(NotificationHelper.DAILY_GOAL_ID, notification)
                Log.d(TAG, "Posted Daily Goal Reminder")
            }
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "study_planner_periodic_tasks"

        fun enqueue(context: Context) {
            // Run every hour to check windows
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS).build()
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
