package com.example.studymateandroidapp.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.studymateandroidapp.MainActivity

import android.media.RingtoneManager
import android.util.Log

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

    private const val TAG = "NotificationHelper"

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

        Log.d(TAG, "Creating notification channels")
        val manager = context.getSystemService(NotificationManager::class.java)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val taskChannel = NotificationChannel(
            CHANNEL_TASK_REMINDER,
            "Task Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for upcoming task deadlines"
            enableVibration(true)
            setSound(soundUri, null)
        }

        val examChannel = NotificationChannel(
            CHANNEL_EXAM_ALERT,
            "Exam Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for upcoming exams"
            enableVibration(true)
            setSound(soundUri, null)
        }

        val dailyChannel = NotificationChannel(
            CHANNEL_DAILY_REMINDER,
            "Daily Study Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminder to keep up your study habit"
            enableVibration(false)
            setSound(soundUri, null)
        }

        val missedTaskChannel = NotificationChannel(
            CHANNEL_MISSED_TASK,
            "Missed Tasks",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alerts for incomplete tasks at the end of the day"
            setSound(soundUri, null)
        }

        val goalChannel = NotificationChannel(
            CHANNEL_DAILY_GOAL,
            "Goal Progress",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily alerts if your study goals aren't met"
            setSound(soundUri, null)
        }

        manager.createNotificationChannels(
            listOf(taskChannel, examChannel, dailyChannel, missedTaskChannel, goalChannel)
        )
        Log.d(TAG, "Notification channels created successfully")
    }

    // ── Builders ──────────────────────────────────────────

    /**
     * Build the morning summary notification.
     */
    fun buildMorningSummary(
        context: Context,
        taskCount: Int
    ): NotificationCompat.Builder {
        Log.d(TAG, "Building morning summary for $taskCount tasks")
        return NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Good Morning!")
            .setContentText("You have $taskCount task(s) due today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build the end of day check notification.
     */
    fun buildEndOfDayCheck(
        context: Context
    ): NotificationCompat.Builder {
        Log.d(TAG, "Building end of day check")
        return NotificationCompat.Builder(context, CHANNEL_MISSED_TASK)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Unfinished Tasks")
            .setContentText("You still have unfinished tasks today :/")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build a task reminder notification.
     */
    fun buildTaskReminder(
        context: Context,
        taskId: Long,
        title: String,
        message: String,
        isCritical: Boolean = false
    ): NotificationCompat.Builder {
        Log.d(TAG, "Building task reminder notification for task $taskId (critical=$isCritical)")
        return NotificationCompat.Builder(context, CHANNEL_TASK_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (isCritical) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(createLaunchIntent(context))
    }

    /**
     * Build an exam alert notification.
     */
    fun buildExamAlert(
        context: Context,
        examId: Long,
        title: String,
        message: String,
        isCritical: Boolean = false
    ): NotificationCompat.Builder {
        Log.d(TAG, "Building exam alert notification for exam $examId (critical=$isCritical)")
        return NotificationCompat.Builder(context, CHANNEL_EXAM_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(if (isCritical) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
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
