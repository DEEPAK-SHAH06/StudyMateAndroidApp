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

        return when {

            // ── Dashboard ──────────────────────────────────────────
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(
                    taskRepository    = TaskRepository(db.taskDao()),
                    sessionRepository = SessionRepository(db.sessionDao()),
                    examRepository    = ExamRepository(db.examDao()),
                    goalRepository    = GoalRepository(db.goalDao()),
                    motivationRepository = MotivationRepository(
                        motivationDao = db.motivationDao(),
                        taskDao       = db.taskDao(),
                        sessionDao    = db.sessionDao(),
                        goalDao       = db.goalDao(),
                        noteDao       = db.noteDao(),
                        flashcardDao  = db.flashcardDao()
                    ),
                    authRepository    = AuthRepository(application),
                    preferenceManager = PreferenceManager(application)
                ) as T

            // ── Calendar ───────────────────────────────────────────
            modelClass.isAssignableFrom(CalendarViewModel::class.java) ->
                CalendarViewModel(
                    taskRepository = TaskRepository(db.taskDao()),
                    examRepository = ExamRepository(db.examDao())
                ) as T

            // ── Tasks ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TaskViewmodel::class.java) ->
                TaskViewmodel(
                    repository = TaskRepository(db.taskDao()),
                    motivationRepository = MotivationRepository(
                        motivationDao = db.motivationDao(),
                        taskDao       = db.taskDao(),
                        sessionDao    = db.sessionDao(),
                        goalDao       = db.goalDao(),
                        noteDao       = db.noteDao(),
                        flashcardDao  = db.flashcardDao()
                    ),
                    reminderScheduler = ReminderScheduler(application),
                    application = application
                ) as T

            // ── Exams ──────────────────────────────────────────────
            modelClass.isAssignableFrom(ExamViewmodel::class.java) ->
                ExamViewmodel(
                    repository = ExamRepository(db.examDao()),
                    studyProgressRepository = StudyProgressRepository(db.studyProgressDao()),
                    reminderScheduler = ReminderScheduler(application),
                    application = application
                ) as T

            // ── Goals ──────────────────────────────────────────────
            modelClass.isAssignableFrom(GoalViewmodel::class.java) ->
                GoalViewmodel(GoalRepository(db.goalDao())) as T

            // ── Notes ──────────────────────────────────────────────
            modelClass.isAssignableFrom(NoteViewmodel::class.java) ->
                NoteViewmodel(
                    repository = NoteRepository(db.noteDao()),
                    application = application
                ) as T

            // ── Flashcards ─────────────────────────────────────────
            modelClass.isAssignableFrom(FlashcardViewmodel::class.java) ->
                FlashcardViewmodel(
                    repository = FlashcardRepository(db.flashcardDao()),
                    application = application
                ) as T

            // ── Timer ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TimerViewmodel::class.java) ->
                TimerViewmodel(
                    sessionRepository = SessionRepository(db.sessionDao()),
                    studyProgressRepository = StudyProgressRepository(db.studyProgressDao()),
                    motivationRepository = MotivationRepository(
                        motivationDao = db.motivationDao(),
                        taskDao       = db.taskDao(),
                        sessionDao    = db.sessionDao(),
                        goalDao       = db.goalDao(),
                        noteDao       = db.noteDao(),
                        flashcardDao  = db.flashcardDao()
                    ),
                    preferenceManager = PreferenceManager(application),
                    context           = application
                ) as T

            // ── Motivation / Reflection / Achievements ─────────────
            modelClass.isAssignableFrom(MotivationViewModel::class.java) ->
                MotivationViewModel(
                    MotivationRepository(
                        motivationDao = db.motivationDao(),
                        taskDao       = db.taskDao(),
                        sessionDao    = db.sessionDao(),
                        goalDao       = db.goalDao(),
                        noteDao       = db.noteDao(),
                        flashcardDao  = db.flashcardDao()
                    )
                ) as T

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
                    authRepository    = AuthRepository(application),
                    preferenceManager = PreferenceManager(application)
                ) as T

            // ── Statistics ─────────────────────────────────────────
            modelClass.isAssignableFrom(StatisticsViewmodel::class.java) ->
                StatisticsViewmodel(
                    StatisticsRepository(
                        taskRepository    = TaskRepository(db.taskDao()),
                        sessionRepository = SessionRepository(db.sessionDao()),
                        goalRepository    = GoalRepository(db.goalDao()),
                        motivationRepository = MotivationRepository(
                            motivationDao = db.motivationDao(),
                            taskDao       = db.taskDao(),
                            sessionDao    = db.sessionDao(),
                            goalDao       = db.goalDao(),
                            noteDao       = db.noteDao(),
                            flashcardDao  = db.flashcardDao()
                        )
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
                    authRepository = AuthRepository(application),
                    syncManager = com.example.studymateandroidapp.utils.sync.SyncManager(application)
                ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}