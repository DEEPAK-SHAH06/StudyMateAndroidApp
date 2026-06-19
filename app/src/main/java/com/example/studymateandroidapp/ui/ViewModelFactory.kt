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

    private var gamificationRepository: GamificationRepository? = null
    private var taskRepository: TaskRepository? = null
    private var sessionRepository: SessionRepository? = null
    private var examRepository: ExamRepository? = null
    private var goalRepository: GoalRepository? = null
    private var motivationRepository: MotivationRepository? = null
    private var studyProgressRepository: StudyProgressRepository? = null
    private var noteRepository: NoteRepository? = null
    private var flashcardRepository: FlashcardRepository? = null

    private fun getGamificationRepository(db: StudyPlannerDatabase) = 
        gamificationRepository ?: GamificationRepository(db.userProgressDao()).also { gamificationRepository = it }

    private fun getTaskRepository(db: StudyPlannerDatabase) = 
        taskRepository ?: TaskRepository(db.taskDao()).also { taskRepository = it }

    private fun getSessionRepository(db: StudyPlannerDatabase) = 
        sessionRepository ?: SessionRepository(db.sessionDao()).also { sessionRepository = it }

    private fun getExamRepository(db: StudyPlannerDatabase) = 
        examRepository ?: ExamRepository(db.examDao()).also { examRepository = it }

    private fun getGoalRepository(db: StudyPlannerDatabase) = 
        goalRepository ?: GoalRepository(db.goalDao()).also { goalRepository = it }

    private fun getMotivationRepository(db: StudyPlannerDatabase): MotivationRepository {
        return motivationRepository ?: MotivationRepository(
            motivationDao = db.motivationDao(),
            taskDao = db.taskDao(),
            sessionDao = db.sessionDao(),
            goalDao = db.goalDao(),
            noteDao = db.noteDao(),
            flashcardDao = db.flashcardDao(),
            gamificationRepository = getGamificationRepository(db)
        ).also { motivationRepository = it }
    }

    private fun getStudyProgressRepository(db: StudyPlannerDatabase) = 
        studyProgressRepository ?: StudyProgressRepository(db.studyProgressDao()).also { studyProgressRepository = it }

    private fun getNoteRepository(db: StudyPlannerDatabase) = 
        noteRepository ?: NoteRepository(db.noteDao()).also { noteRepository = it }

    private fun getFlashcardRepository(db: StudyPlannerDatabase) = 
        flashcardRepository ?: FlashcardRepository(db.flashcardDao()).also { flashcardRepository = it }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = extras[APPLICATION_KEY]!!
        val db          = StudyPlannerDatabase.getInstance(application)

        return when {

            // ── Dashboard ──────────────────────────────────────────
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(
                    taskRepository    = getTaskRepository(db),
                    sessionRepository = getSessionRepository(db),
                    examRepository    = getExamRepository(db),
                    goalRepository    = getGoalRepository(db),
                    motivationRepository = getMotivationRepository(db),
                    authRepository    = AuthRepository(application),
                    preferenceManager = PreferenceManager(application)
                ) as T

            // ── Calendar ───────────────────────────────────────────
            modelClass.isAssignableFrom(CalendarViewModel::class.java) ->
                CalendarViewModel(
                    taskRepository =  getTaskRepository(db),
                    examRepository = getExamRepository(db)
                ) as T

            // ── Tasks ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TaskViewmodel::class.java) ->
                TaskViewmodel(
                    repository = getTaskRepository(db),
                    motivationRepository = getMotivationRepository(db),
                    reminderScheduler = ReminderScheduler(application),
                    application = application
                ) as T

            // ── Exams ──────────────────────────────────────────────
            modelClass.isAssignableFrom(ExamViewmodel::class.java) ->
                ExamViewmodel(
                    repository = getExamRepository(db),
                    studyProgressRepository = getStudyProgressRepository(db),
                    reminderScheduler = ReminderScheduler(application),
                    application = application
                ) as T

            // ── Goals ──────────────────────────────────────────────
            modelClass.isAssignableFrom(GoalViewmodel::class.java) ->
                GoalViewmodel(
                    repository = getGoalRepository(db),
                    motivationRepository = getMotivationRepository(db)
                ) as T

            // ── Notes ──────────────────────────────────────────────
            modelClass.isAssignableFrom(NoteViewmodel::class.java) ->
                NoteViewmodel(
                    repository = getNoteRepository(db),
                    examRepository = getExamRepository(db),
                    application = application
                ) as T

            // ── Flashcards ─────────────────────────────────────────
            modelClass.isAssignableFrom(FlashcardViewmodel::class.java) ->
                FlashcardViewmodel(
                    repository = getFlashcardRepository(db),
                    motivationRepository = getMotivationRepository(db),
                    application = application
                ) as T

            // ── Timer ──────────────────────────────────────────────
            modelClass.isAssignableFrom(TimerViewmodel::class.java) ->
                TimerViewmodel(
                    sessionRepository = getSessionRepository(db),
                    studyProgressRepository = getStudyProgressRepository(db),
                    motivationRepository = getMotivationRepository(db),
                    preferenceManager = PreferenceManager(application),
                    context           = application
                ) as T

            // ── Motivation / Reflection / Achievements ─────────────
            modelClass.isAssignableFrom(MotivationViewModel::class.java) ->
                MotivationViewModel(getMotivationRepository(db)) as T

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
                        taskRepository    = getTaskRepository(db),
                        sessionRepository = getSessionRepository(db),
                        goalRepository    = getGoalRepository(db),
                        motivationRepository = getMotivationRepository(db),
                        gamificationRepository = getGamificationRepository(db)
                    )
                ) as T

            // ── Gamification Global ────────────────────────────────
            modelClass.isAssignableFrom(GamificationViewModel::class.java) ->
                GamificationViewModel(getGamificationRepository(db)) as T

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