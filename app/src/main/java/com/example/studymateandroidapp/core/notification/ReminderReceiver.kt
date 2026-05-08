package com.example.studymateandroidapp.core.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * BroadcastReceiver that fires when an AlarmManager alarm triggers.
 *
 * ## How it works
 * 1. [ReminderScheduler] sets an exact alarm with a PendingIntent targeting this receiver.
 * 2. When the alarm fires, [onReceive] reads extras to determine the notification type.
 * 3. It builds the appropriate notification via [NotificationHelper] and posts it.
 *
 * ## Intent Extras
 * | Key | Type | Description |
 * |-----|------|-------------|
 * | `type` | String | `"task"`, `"exam"` |
 * | `entity_id` | Long | Task or Exam ID |
 * | `title` | String | Title to display |
 * | `message` | String | Contextual message (e.g. "Due today") |
 */
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TYPE = "type"
        const val EXTRA_ENTITY_ID = "entity_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"

        const val TYPE_TASK = "task"
        const val TYPE_EXAM = "exam"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE) ?: return
        val entityId = intent.getLongExtra(EXTRA_ENTITY_ID, -1L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: ""

        val manager = context.getSystemService<NotificationManager>() ?: return
        val pendingResult = goAsync()

        // Focus Mode check
        val database = com.studyplanner.core.database.StudyPlannerDatabase.getInstance(context)
        val reminderDao = database.reminderDao()
        
        // Use a coroutine to check focus mode
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val focusSetting = reminderDao.getSettingByType(com.studyplanner.core.model.ReminderType.FOCUS_MODE)
                if (focusSetting?.isEnabled == true) return@launch

                withContext(Dispatchers.Main) {
                    when (type) {
                        TYPE_TASK -> {
                            val notificationId = NotificationHelper.TASK_NOTIFICATION_BASE + entityId.toInt()
                            val notification = NotificationHelper.buildTaskReminder(
                                context = context,
                                taskId = entityId,
                                title = title,
                                dueInfo = message
                            ).build()
                            manager.notify(notificationId, notification)
                        }

                        TYPE_EXAM -> {
                            val notificationId = NotificationHelper.EXAM_NOTIFICATION_BASE + entityId.toInt()
                            val notification = NotificationHelper.buildExamAlert(
                                context = context,
                                examId = entityId,
                                title = title,
                                timeInfo = message
                            ).build()
                            manager.notify(notificationId, notification)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
