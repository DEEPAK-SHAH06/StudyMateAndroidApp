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
import android.util.Log

/**
 * Schedules and cancels exact-time reminders via [AlarmManager].
 *
 * Implements a Multi-Stage Reminder Strategy:
 * - Tasks: -1h, -15m, 0, +5m (Overdue)
 * - Exams: -3d, -1d, Day-of @ 8AM, -1h, -15m
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val TAG = "ReminderScheduler"

    // ── Task Reminders ────────────────────────────────────

    fun scheduleTaskReminders(
        taskId: Long,
        title: String,
        dueDate: LocalDate,
        dueTime: LocalTime
    ) {
        val target = LocalDateTime.of(dueDate, dueTime)
        Log.d(TAG, "Scheduling multi-stage reminders for Task $taskId: $title at $target")

        // 1. One Hour Before
        scheduleTaskStage(taskId, title, target.minusHours(1), "is due in 1 hour", STAGE_TASK_1H)

        // 2. 15 Minutes Before
        scheduleTaskStage(taskId, title, target.minusMinutes(15), "is due in 15 minutes...", STAGE_TASK_15M)

        // 3. At Due Time
        scheduleTaskStage(taskId, title, target, "is due now!", STAGE_TASK_DUE, isCritical = true)

        // 4. 5 Minutes After (Overdue)
        scheduleTaskStage(taskId, title, target.plusMinutes(5), "You missed your $title :(", STAGE_TASK_OVERDUE, isCritical = true, action = ReminderReceiver.ACTION_MARK_OVERDUE)
    }

    private fun scheduleTaskStage(
        taskId: Long,
        title: String,
        triggerTime: LocalDateTime,
        message: String,
        stageOffset: Int,
        isCritical: Boolean = false,
        action: String? = null
    ) {
        val triggerMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        if (triggerMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Smart Skip: Task $taskId stage $stageOffset is in the past ($triggerTime)")
            return
        }

        val requestCode = BASE_TASK + stageOffset + taskId.toInt()
        val pendingIntent = createPendingIntent(
            requestCode = requestCode,
            type = ReminderReceiver.TYPE_TASK,
            entityId = taskId,
            title = if (isCritical) title else "Task Due Soon",
            message = if (isCritical) message else "$title $message",
            action = action
        )

        scheduleExactAlarm(triggerMillis, pendingIntent, "Task $taskId Stage $stageOffset")
    }

    fun cancelTaskReminders(taskId: Long) {
        Log.d(TAG, "Cancelling all reminders for task $taskId")
        listOf(STAGE_TASK_1H, STAGE_TASK_15M, STAGE_TASK_DUE, STAGE_TASK_OVERDUE).forEach { stage ->
            val requestCode = BASE_TASK + stage + taskId.toInt()
            alarmManager.cancel(createEmptyPendingIntent(requestCode))
        }
    }

    // ── Exam Notifications ────────────────────────────────

    fun scheduleExamReminders(
        examId: Long,
        title: String,
        subject: String,
        examDateTime: LocalDateTime,
        isTimeSet: Boolean
    ) {
        Log.d(TAG, "Scheduling multi-stage reminders for Exam $examId: $title ($subject) at $examDateTime")

        // 1. 3 Days Before (8 AM)
        val stage1Time = LocalDateTime.of(examDateTime.toLocalDate().minusDays(3), LocalTime.of(8, 0))
        scheduleExamStage(examId, subject, stage1Time, "Your $subject exam is in 3 days.", STAGE_EXAM_3D)

        // 2. 1 Day Before (8 AM)
        val stage2Time = LocalDateTime.of(examDateTime.toLocalDate().minusDays(1), LocalTime.of(8, 0))
        scheduleExamStage(examId, subject, stage2Time, "$subject exam is tomorrow. Time for the final preparation!", STAGE_EXAM_1D)

        // 3. Morning of Exam (8 AM)
        val stage3Time = LocalDateTime.of(examDateTime.toLocalDate(), LocalTime.of(8, 0))
        scheduleExamStage(examId, subject, stage3Time, "Good luck! You've got $subject today.", STAGE_EXAM_MORNING, isCritical = true)

        if (isTimeSet) {
            // 4. One Hour Before
            scheduleExamStage(examId, subject, examDateTime.minusHours(1), "Final Revision? $subject starts in 1 hour.", STAGE_EXAM_1H, isCritical = true)

            // 5. 15 Minutes Before
            scheduleExamStage(examId, subject, examDateTime.minusMinutes(15), "You've Got This! $subject starts in 15 minutes.", STAGE_EXAM_15M, isCritical = true)
        }
    }

    private fun scheduleExamStage(
        examId: Long,
        subject: String,
        triggerTime: LocalDateTime,
        message: String,
        stageOffset: Int,
        isCritical: Boolean = false
    ) {
        val triggerMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        if (triggerMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Smart Skip: Exam $examId stage $stageOffset is in the past ($triggerTime)")
            return
        }

        val requestCode = BASE_EXAM + stageOffset + examId.toInt()
        val title = when(stageOffset) {
            STAGE_EXAM_3D -> "Exam Coming Up"
            STAGE_EXAM_1D -> "Exam Tomorrow"
            STAGE_EXAM_MORNING -> "Exam Day"
            STAGE_EXAM_1H -> "Final Revision?"
            STAGE_EXAM_15M -> "You've Got This!"
            else -> "Exam Alert"
        }

        val pendingIntent = createPendingIntent(
            requestCode = requestCode,
            type = ReminderReceiver.TYPE_EXAM,
            entityId = examId,
            title = title,
            message = message
        )

        scheduleExactAlarm(triggerMillis, pendingIntent, "Exam $examId Stage $stageOffset")
    }

    fun cancelExamReminders(examId: Long) {
        Log.d(TAG, "Cancelling all reminders for exam $examId")
        listOf(STAGE_EXAM_3D, STAGE_EXAM_1D, STAGE_EXAM_MORNING, STAGE_EXAM_1H, STAGE_EXAM_15M).forEach { stage ->
            val requestCode = BASE_EXAM + stage + examId.toInt()
            alarmManager.cancel(createEmptyPendingIntent(requestCode))
        }
    }

    // ── Private Helpers ───────────────────────────────────

    private fun scheduleExactAlarm(triggerMillis: Long, pendingIntent: PendingIntent, tag: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    Log.d(TAG, "Scheduling EXACT alarm for $tag at $triggerMillis")
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                } else {
                    Log.w(TAG, "Permission missing for EXACT alarm. Falling back for $tag")
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for $tag", e)
        }
    }

    private fun createPendingIntent(
        requestCode: Int,
        type: String,
        entityId: Long,
        title: String,
        message: String,
        action: String? = null
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TYPE, type)
            putExtra(ReminderReceiver.EXTRA_ENTITY_ID, entityId)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, message)
            action?.let { this.action = it }
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createEmptyPendingIntent(requestCode: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val BASE_TASK = 1_000_000
        private const val STAGE_TASK_1H = 100_000
        private const val STAGE_TASK_15M = 150_000
        private const val STAGE_TASK_DUE = 0
        private const val STAGE_TASK_OVERDUE = 50_000

        private const val BASE_EXAM = 2_000_000
        private const val STAGE_EXAM_3D = 300_000
        private const val STAGE_EXAM_1D = 100_000
        private const val STAGE_EXAM_MORNING = 0
        private const val STAGE_EXAM_1H = 60_000
        private const val STAGE_EXAM_15M = 15_000
    }
}
