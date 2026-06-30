package com.example.studymateandroidapp

import android.app.Application
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.repository.*
import com.example.studymateandroidapp.utils.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyMateApplication : Application() {

    val statisticsRepository: StatisticsRepository by lazy {
        val db = StudyPlannerDatabase.getInstance(this)
        val gamificationRepository = GamificationRepository(db.userProgressDao())
        
        StatisticsRepository(
            taskRepository = TaskRepository(db.taskDao()),
            sessionRepository = SessionRepository(db.sessionDao()),
            goalRepository = GoalRepository(db.goalDao(), this),
            motivationRepository = MotivationRepository(
                motivationDao = db.motivationDao(),
                taskDao = db.taskDao(),
                sessionDao = db.sessionDao(),
                goalDao       = db.goalDao(),
                noteDao       = db.noteDao(),
                flashcardDao  = db.flashcardDao(),
                gamificationRepository = gamificationRepository,
                context = this
            ),
            gamificationRepository = gamificationRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)

        // Asynchronously warm up Room database / SQLCipher to avoid main thread block on first access
        CoroutineScope(Dispatchers.IO).launch {
            StudyPlannerDatabase.preloadDatabase(this@StudyMateApplication)
        }
    }
}
