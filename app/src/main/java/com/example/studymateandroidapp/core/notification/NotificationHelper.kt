package com.example.studymateandroidapp.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.studyplanner.MainActivity

/**
 * Centralized notification helper.
 *
 * Creates notification channels on first use and provides
 * builder functions for each notification type:
 * - Task reminders (exact time, AlarmManager)
 * - Exam notifications (exact time, AlarmManager)
 * - Daily study reminders (periodic, WorkManager)
 *
 * ## Channel Strategy
 * Three separate channels with distinct importance levels:
 * | Channel | Importance | Sound | Vibrate |
 * |---------|-----------|-------|---------|
 * | Task Reminders | HIGH | Yes | Yes |
 * | Exam Alerts | HIGH | Yes | Yes |
 * | Daily Reminder | DEFAULT | Yes | No |
 */
object NotificationHelper {

    // ── Channel IDs ───────────────────────────────────────
    const val CHANNEL_TASK_REMINDER = "task_reminders"
    const val CHANNEL_EXAM_ALERT = "exam_alerts"
    const val CHANNEL_DAILY_REMINDER = "daily_study_reminder"
    const val CHANNEL_MISSED_TASK = "missed_tasks"
    const val CHANNEL_DAILY_GOAL = "daily_goal_alert"

    // ── Notification IDs (base; actual ID = base + entityId) ──
    const val TASK_NOTIFICATION_BASE = 1000
    const val EXAM_NOTIFICATION_BASE = 2000
    const val DAILY_REMINDER_ID = 3000
    const val MISSED_TASK_ID = 4000
    const val DAILY_GOAL_ID = 5000

    /**
     * Create all notification channels.
     * Safe to call multiple times — Android ignores duplicates.
     * Call this from `Application.onCreate()`.
     */
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val taskChannel = NotificationChannel(
            CHANNEL_TASK_REMINDER,
            "Task Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for upcoming task deadlines"
            enableVibration(true)
        }

        val examChannel = NotificationChannel(
            CHANNEL_EXAM_ALERT,
            "Exam Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for upcoming exams"
            enableVibration(true)
        }

        val dailyChannel = NotificationChannel(
            CHANNEL_DAILY_REMINDER,
            "Daily Study Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminder to keep up your study habit"
            enableVibration(false)
        }

        val missedTaskChannel = NotificationChannel(
            CHANNEL_MISSED_TASK,
            "Missed Tasks",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alerts for incomplete tasks at the end of the day"
        }

        val goalChannel = NotificationChannel(
            CHANNEL_DAILY_GOAL,
            "Goal Progress",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily alerts if your study goals aren't met"
        }

        manager.createNotificationChannels(
            listOf(taskChannel, examChannel, dailyChannel, missedTaskChannel, goalChannel)
        )
    }

    // ── Builders ──────────────────────────────────────────

    /**
     * Build a task reminder notification.
     *
     * @param taskId    Used to generate a unique notification ID
     * @param title     Task title
     * @param dueInfo   e.g. "Due today" or "Due in 1 hour"
     */
    fun buildTaskReminder(
        context: Context,
        taskId: Long,
        title: String,
        dueInfo: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_TASK_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Task Reminder")
            .setContentText("$title — $dueInfo")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build an exam alert notification.
     *
     * @param examId    Used to generate a unique notification ID
     * @param title     Exam title
     * @param timeInfo  e.g. "Tomorrow" or "In 3 days"
     */
    fun buildExamAlert(
        context: Context,
        examId: Long,
        title: String,
        timeInfo: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_EXAM_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Exam Alert: $title")
            .setContentText("$title is $timeInfo")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$title is $timeInfo. Make sure you're prepared!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build the daily study reminder notification.
     */
    fun buildDailyReminder(
        context: Context,
        studyMinutesToday: Int
    ): NotificationCompat.Builder {
        val message = if (studyMinutesToday > 0) {
            "You've studied ${studyMinutesToday}m today. Keep the momentum going!"
        } else {
            "You haven't studied yet today. Start a session now!"
        }

        return NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Study Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build a notification for missed tasks.
     */
    fun buildMissedTaskAlert(
        context: Context,
        count: Int
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_MISSED_TASK)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Missed Tasks")
            .setContentText("You have $count incomplete tasks due today. Try to finish them!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build a notification for missed daily study goal.
     */
    fun buildDailyGoalAlert(
        context: Context,
        remainingMinutes: Int
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_DAILY_GOAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Goal Not Met")
            .setContentText("You are $remainingMinutes minutes short of your daily study goal!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    // ── Helpers ───────────────────────────────────────────

    private fun createLaunchIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
