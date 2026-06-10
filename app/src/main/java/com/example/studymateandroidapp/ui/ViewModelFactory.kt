package com.example.studymateandroidapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.repository.*
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import com.example.studymateandroidapp.viewmodel.*

object ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = extras[APPLICATION_KEY]!!
        val db          = StudyPlannerDatabase.getInstance(application)

        // Shared Repositories
        val taskRepo = TaskRepository(db.taskDao())
        val sessionRepo = SessionRepository(db.sessionDao())
        val examRepo = ExamRepository(db.examDao())
        val goalRepo = GoalRepository(db.goalDao())
        val noteRepo = NoteRepository(db.noteDao())
        val flashcardRepo = FlashcardRepository(db.flashcardDao())
        val motivationRepo = MotivationRepository(
            motivationDao = db.motivationDao(),
            taskDao       = db.taskDao(),
            sessionDao    = db.sessionDao(),
            goalDao       = db.goalDao(),
            noteDao       = db.noteDao(),
            flashcardDao  = db.flashcardDao()
        )
        val authRepo = AuthRepository(application)
        val prefManager = PreferenceManager(application)

        return when {
            // ── Dashboard ──────────────────────────────────────────
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(
                    taskRepository       = taskRepo,
                    sessionRepository    = sessionRepo,
                    examRepository       = examRepo,
                    goalRepository       = goalRepo,
                    motivationRepository = motivationRepo,
                    authRepository       = authRepo,
                    preferenceManager    = prefManager
                ) as T

            // ── Tasks ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TaskViewmodel::class.java) ->
                TaskViewmodel(
                    repository = taskRepo,
                    motivationRepository = motivationRepo,
                    reminderScheduler = ReminderScheduler(application)
                ) as T

            // ── Exams ──────────────────────────────────────────────
            modelClass.isAssignableFrom(ExamViewmodel::class.java) ->
                ExamViewmodel(
                    repository = examRepo,
                    reminderScheduler = ReminderScheduler(application)
                ) as T

            // ── Goals ──────────────────────────────────────────────
            modelClass.isAssignableFrom(GoalViewmodel::class.java) ->
                GoalViewmodel(goalRepo) as T

            // ── Notes ──────────────────────────────────────────────
            modelClass.isAssignableFrom(NoteViewmodel::class.java) ->
                NoteViewmodel(noteRepo) as T

            // ── Flashcards ─────────────────────────────────────────
            modelClass.isAssignableFrom(FlashcardViewmodel::class.java) ->
                FlashcardViewmodel(flashcardRepo, motivationRepo) as T

            // ── Timer ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TimerViewmodel::class.java) ->
                TimerViewmodel(
                    sessionRepository = sessionRepo,
                    motivationRepository = motivationRepo,
                    preferenceManager = prefManager,
                    context           = application
                ) as T

            // ── Motivation / Reflection / Achievements ─────────────
            modelClass.isAssignableFrom(MotivationViewModel::class.java) ->
                MotivationViewModel(motivationRepo) as T

            // ── Settings ───────────────────────────────────────────
            modelClass.isAssignableFrom(SettingViewmodel::class.java) ->
                SettingViewmodel(
                    notificationRepository = NotificationRepository(
                        context = application,
                        reminderDao = db.reminderDao(),
                        taskDao = db.taskDao(),
                        examDao = db.examDao(),
                        scheduler = ReminderScheduler(application)
                    ),
                    authRepository    = authRepo,
                    preferenceManager = prefManager
                ) as T

            // ── Statistics ─────────────────────────────────────────
            modelClass.isAssignableFrom(StatisticsViewmodel::class.java) ->
                StatisticsViewmodel(
                    StatisticsRepository(
                        taskRepository       = taskRepo,
                        sessionRepository    = sessionRepo,
                        goalRepository       = goalRepo,
                        motivationRepository = motivationRepo
                    )
                ) as T

            // ── Legacy stubs (keep until teammate screens are done) ─
            modelClass.isAssignableFrom(ReflectionViewmodel::class.java) ->
                ReflectionViewmodel() as T

            modelClass.isAssignableFrom(AchievementViewmodel::class.java) ->
                AchievementViewmodel() as T

            // ── Authentication ─────────────────────────────────────
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(
                    authRepository = authRepo,
                    syncManager = com.example.studymateandroidapp.utils.sync.SyncManager(application)
                ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
