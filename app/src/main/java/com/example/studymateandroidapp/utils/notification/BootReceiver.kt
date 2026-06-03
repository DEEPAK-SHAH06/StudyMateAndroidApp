package com.example.studymateandroidapp.utils.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * BroadcastReceiver triggered on device reboot.
 *
 * ## Why is this needed?
 * AlarmManager alarms are **lost on device reboot**. This receiver
 * re-schedules all pending task and exam reminders from Room data.
 *
 * ## Manifest registration
 * ```xml
 * <receiver
 *     android:name=".core.notification.BootReceiver"
 *     android:exported="false">
 *     <intent-filter>
 *         <action android:name="android.intent.action.BOOT_COMPLETED" />
 *     </intent-filter>
 * </receiver>
 * ```
 *
 * ## Required permission
 * ```xml
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * ```
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // Use goAsync() since we need to do coroutine work
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rescheduleAll(context)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun rescheduleAll(context: Context) {
        val database = StudyPlannerDatabase.getInstance(context)
        val scheduler = ReminderScheduler(context)
        val repository = NotificationRepository(
            context = context,
            reminderDao = database.reminderDao(),
            taskDao = database.taskDao(),
            examDao = database.examDao(),
            scheduler = scheduler
        )

        repository.rescheduleAll()
    }
}
