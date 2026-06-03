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
                TaskViewmodel(TaskRepository(db.taskDao())) as T

            // ── Exams ──────────────────────────────────────────────
            modelClass.isAssignableFrom(ExamViewmodel::class.java) ->
                ExamViewmodel(ExamRepository(db.examDao())) as T

            // ── Goals ──────────────────────────────────────────────
            modelClass.isAssignableFrom(GoalViewmodel::class.java) ->
                GoalViewmodel(GoalRepository(db.goalDao())) as T

            // ── Notes ──────────────────────────────────────────────
            modelClass.isAssignableFrom(NoteViewmodel::class.java) ->
                NoteViewmodel(NoteRepository(db.noteDao())) as T

            // ── Flashcards ─────────────────────────────────────────
            modelClass.isAssignableFrom(FlashcardViewmodel::class.java) ->
                FlashcardViewmodel(FlashcardRepository(db.flashcardDao())) as T

            // ── Timer ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TimerViewmodel::class.java) ->
                TimerViewmodel(SessionRepository(db.sessionDao())) as T

            // ── Motivation / Reflection / Achievements ─────────────
            modelClass.isAssignableFrom(MotivationViewModel::class.java) ->
                MotivationViewModel(
                    MotivationRepository(
                        motivationDao = db.motivationDao(),
                        taskDao       = db.taskDao(),
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
                        goalRepository    = GoalRepository(db.goalDao())
                    )
                ) as T

            // ── Legacy stubs (keep until teammate screens are done) ─
            modelClass.isAssignableFrom(ReflectionViewmodel::class.java) ->
                ReflectionViewmodel() as T

            modelClass.isAssignableFrom(AchievementViewmodel::class.java) ->
                AchievementViewmodel() as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}