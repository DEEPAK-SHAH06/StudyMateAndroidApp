package com.example.studymateandroidapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.studymateandroidapp.core.auth.AuthRepository
import com.example.studymateandroidapp.core.preferences.PreferenceManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.studymateandroidapp.core.database.StudyPlannerDatabase
import com.example.studymateandroidapp.feature.dashboard.viewmodel.DashboardViewModel
import com.example.studymateandroidapp.feature.calendar.viewmodel.CalendarViewModel
import com.example.studymateandroidapp.feature.exam.data.ExamRepository
import com.example.studymateandroidapp.feature.exam.viewmodel.ExamViewmodel
import com.example.studymateandroidapp.feature.flashcard.data.FlashcardRepository
import com.example.studymateandroidapp.feature.flashcard.viewmodel.FlashcardViewmodel
import com.example.studymateandroidapp.feature.motivation.viewmodel.MotivationViewModel
import com.example.studymateandroidapp.feature.settings.viewmodel.SettingViewmodel
import com.example.studymateandroidapp.feature.statistics.viewmodel.StatisticsViewmodel
import com.example.studymateandroidapp.feature.task.data.TaskRepository
import com.example.studymateandroidapp.feature.task.viewmodel.TaskViewmodel
import com.example.studymateandroidapp.feature.timer.viewmodel.TimerViewmodel
import com.example.studymateandroidapp.feature.reflection.viewmodel.ReflectionViewmodel
import com.example.studymateandroidapp.feature.achievements.viewmodel.AchievementViewmodel
import com.example.studymateandroidapp.feature.goal.viewmodel.GoalViewmodel
import com.example.studymateandroidapp.feature.note.viewmodel.NoteViewmodel
import com.example.studymateandroidapp.feature.goal.data.GoalRepository
import com.example.studymateandroidapp.feature.note.data.NoteRepository
import com.example.studymateandroidapp.feature.motivation.data.MotivationRepository

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // In a real app, these would come from a Dependency Injection container or Application class
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(
                    authRepository = AuthRepository(),
                    preferenceManager = PreferenceManager()
                ) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> CalendarViewModel() as T
            modelClass.isAssignableFrom(ExamViewmodel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                val database = StudyPlannerDatabase.getInstance(application)
                ExamViewmodel(ExamRepository(database.examDao())) as T
            }
            modelClass.isAssignableFrom(FlashcardViewmodel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                val database = StudyPlannerDatabase.getInstance(application)
                FlashcardViewmodel(FlashcardRepository(database.flashcardDao())) as T
            }
            modelClass.isAssignableFrom(MotivationViewModel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                val database = StudyPlannerDatabase.getInstance(application)
                MotivationViewModel(MotivationRepository(database.motivationDao())) as T
            }
            modelClass.isAssignableFrom(SettingViewmodel::class.java) -> SettingViewmodel() as T
            modelClass.isAssignableFrom(StatisticsViewmodel::class.java) -> StatisticsViewmodel() as T
            modelClass.isAssignableFrom(TaskViewmodel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                val database = StudyPlannerDatabase.getInstance(application)
                TaskViewmodel(TaskRepository(database.taskDao())) as T
            }
            modelClass.isAssignableFrom(TimerViewmodel::class.java) -> TimerViewmodel() as T
            modelClass.isAssignableFrom(ReflectionViewmodel::class.java) -> ReflectionViewmodel() as T
            modelClass.isAssignableFrom(AchievementViewmodel::class.java) -> AchievementViewmodel() as T
            modelClass.isAssignableFrom(GoalViewmodel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                val database = StudyPlannerDatabase.getInstance(application)
                GoalViewmodel(GoalRepository(database.goalDao())) as T
            }
            modelClass.isAssignableFrom(NoteViewmodel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                val database = StudyPlannerDatabase.getInstance(application)
                NoteViewmodel(NoteRepository(database.noteDao())) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
