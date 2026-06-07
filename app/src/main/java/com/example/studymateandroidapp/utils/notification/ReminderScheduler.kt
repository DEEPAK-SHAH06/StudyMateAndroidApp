package com.example.studymateandroidapp.utils.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Schedules and cancels exact-time reminders via [AlarmManager].
 *
 * Used for:
 * - **Task reminders**: fires at a specific time before the task's due date
 * - **Exam notifications**: fires 1 day and 3 days before the exam date
 *
 * ## Alarm ID Strategy
 * Each alarm needs a unique [PendingIntent] request code:
 * - Task reminders:  `1_000_000 + taskId`
 * - Exam (1-day):    `2_000_000 + examId`
 * - Exam (3-day):    `3_000_000 + examId`
 *
 * This prevents collisions across notification types.
 *
 * ## Android 12+ Exact Alarm Permission
 * Starting API 31, `SCHEDULE_EXACT_ALARM` must be declared in the manifest
 * and the user may need to grant it. This class checks [canScheduleExactAlarms]
 * before scheduling and falls back to inexact alarms if denied.
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val TAG = "ReminderScheduler"

    // ── Task Reminders ────────────────────────────────────

    /**
     * Schedule a reminder for a task at a specific date & time.
     *
     * @param taskId    Unique task identifier
     * @param title     Task title (shown in notification)
     * @param dueDate   The task's due date
     * @param reminderTime  When to fire the reminder (e.g. 9:00 AM on due date)
     */
    fun scheduleTaskReminder(
        taskId: Long,
        title: String,
        dueDate: LocalDate,
        reminderTime: LocalTime = LocalTime.of(9, 0) // default: 9 AM on due date
    ) {
        val triggerDateTime = LocalDateTime.of(dueDate, reminderTime)
        val triggerMillis = triggerDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        android.util.Log.d(TAG, "Scheduling task reminder: id=$taskId, title=$title, time=$triggerDateTime")

        // Don't schedule if the time is already past
        if (triggerMillis <= System.currentTimeMillis()) {
            android.util.Log.w(TAG, "Skipping task reminder: trigger time is in the past")
            return
        }

        val requestCode = TASK_REQUEST_CODE_BASE + taskId.toInt()
        val pendingIntent = createPendingIntent(
            requestCode = requestCode,
            type = ReminderReceiver.TYPE_TASK,
            entityId = taskId,
            title = title,
            message = "Due today"
        )

        scheduleExactAlarm(triggerMillis, pendingIntent)
    }

    /** Cancel a previously scheduled task reminder. */
    fun cancelTaskReminder(taskId: Long) {
        val requestCode = TASK_REQUEST_CODE_BASE + taskId.toInt()
        val pendingIntent = createPendingIntent(
            requestCode = requestCode,
            type = ReminderReceiver.TYPE_TASK,
            entityId = taskId,
            title = "",
            message = ""
        )
        alarmManager.cancel(pendingIntent)
    }

    // ── Exam Notifications ────────────────────────────────

    /**
     * Schedule exam reminders for specific days before the exam.
     *
     * @param examId       Unique exam identifier
     * @param title        Exam title
     * @param examDate     Date of the exam
     * @param daysBefore   List of days before the exam to fire a reminder
     */
    fun scheduleExamReminders(
        examId: Long,
        title: String,
        examDate: LocalDate,
        daysBefore: List<Int> = listOf(1, 3)
    ) {
        daysBefore.forEach { days ->
            val message = when (days) {
                0 -> "today"
                1 -> "tomorrow"
                else -> "in $days days"
            }
            scheduleExamAlarm(
                examId = examId,
                title = title,
                triggerDate = examDate.minusDays(days.toLong()),
                message = message,
                requestCodeBase = EXAM_BASE_CODE(days)
            )
        }
    }

    /** Cancel all scheduled exam reminders for a given exam. */
    fun cancelExamReminders(examId: Long) {
        // Cancel for common "days before" (0, 1, 2, 3, 7)
        listOf(0, 1, 2, 3, 7).forEach { days ->
            val requestCode = EXAM_BASE_CODE(days) + examId.toInt()
            val pendingIntent = createPendingIntent(
                requestCode = requestCode,
                type = ReminderReceiver.TYPE_EXAM,
                entityId = examId,
                title = "",
                message = ""
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    // ── Private Helpers ───────────────────────────────────

    private fun scheduleExamAlarm(
        examId: Long,
        title: String,
        triggerDate: LocalDate,
        message: String,
        requestCodeBase: Int
    ) {
        val triggerMillis = LocalDateTime.of(triggerDate, LocalTime.of(9, 0))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (triggerMillis <= System.currentTimeMillis()) return

        val requestCode = requestCodeBase + examId.toInt()
        val pendingIntent = createPendingIntent(
            requestCode = requestCode,
            type = ReminderReceiver.TYPE_EXAM,
            entityId = examId,
            title = title,
            message = message
        )

        scheduleExactAlarm(triggerMillis, pendingIntent)
    }

    /**
     * Schedule an exact alarm, with fallback to inexact on API 31+
     * if the exact-alarm permission is not granted.
     */
    private fun scheduleExactAlarm(triggerMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                // Fallback: inexact alarm (may be delayed up to ~10 min)
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    private fun createPendingIntent(
        requestCode: Int,
        type: String,
        entityId: Long,
        title: String,
        message: String
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TYPE, type)
            putExtra(ReminderReceiver.EXTRA_ENTITY_ID, entityId)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val TASK_REQUEST_CODE_BASE = 1_000_000
        private fun EXAM_BASE_CODE(daysBefore: Int) = 2_000_000 + (daysBefore * 100_000)
    }
}
