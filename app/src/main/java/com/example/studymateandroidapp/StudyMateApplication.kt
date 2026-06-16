package com.example.studymateandroidapp

import android.app.Application
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.repository.*
import com.example.studymateandroidapp.utils.notification.NotificationHelper

class StudyMateApplication : Application() {

    lateinit var statisticsRepository: StatisticsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)

        val db = StudyPlannerDatabase.getInstance(this)
        val gamificationRepository = GamificationRepository(db.userProgressDao())
        
        statisticsRepository = StatisticsRepository(
            taskRepository = TaskRepository(db.taskDao()),
            sessionRepository = SessionRepository(db.sessionDao()),
            goalRepository = GoalRepository(db.goalDao()),
            motivationRepository = MotivationRepository(
                motivationDao = db.motivationDao(),
                taskDao = db.taskDao(),
                sessionDao = db.sessionDao(),
                goalDao       = db.goalDao(),
                noteDao       = db.noteDao(),
                flashcardDao  = db.flashcardDao(),
                gamificationRepository = gamificationRepository
                ),
            gamificationRepository = gamificationRepository
        )
    }
}
