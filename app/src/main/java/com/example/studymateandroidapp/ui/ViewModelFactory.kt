package com.example.studymateandroidapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.studymateandroidapp.data.repository.AuthRepository
import com.example.studymateandroidapp.data.local.PreferenceManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.viewmodel.DashboardViewModel
import com.example.studymateandroidapp.viewmodel.CalendarViewModel
import com.example.studymateandroidapp.data.repository.ExamRepository
import com.example.studymateandroidapp.viewmodel.ExamViewmodel
import com.example.studymateandroidapp.data.repository.FlashcardRepository
import com.example.studymateandroidapp.viewmodel.FlashcardViewmodel
import com.example.studymateandroidapp.viewmodel.MotivationViewModel
import com.example.studymateandroidapp.viewmodel.SettingViewmodel
import com.example.studymateandroidapp.viewmodel.StatisticsViewmodel
import com.example.studymateandroidapp.data.repository.TaskRepository
import com.example.studymateandroidapp.viewmodel.TaskViewmodel
import com.example.studymateandroidapp.viewmodel.TimerViewmodel
import com.example.studymateandroidapp.viewmodel.ReflectionViewmodel
import com.example.studymateandroidapp.viewmodel.AchievementViewmodel
import com.example.studymateandroidapp.viewmodel.GoalViewmodel
import com.example.studymateandroidapp.viewmodel.NoteViewmodel
import com.example.studymateandroidapp.data.repository.GoalRepository
import com.example.studymateandroidapp.data.repository.NoteRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // In a real app, these would come from a Dependency Injection container or Application class
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                val application = extras[APPLICATION_KEY]!!
                DashboardViewModel(
                    authRepository = AuthRepository(application),
                    preferenceManager = PreferenceManager(application)
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
                MotivationViewModel(
                    MotivationRepository(
                        motivationDao = database.motivationDao(),
                        taskDao = database.taskDao(),
                        goalDao = database.goalDao(),
                        noteDao = database.noteDao(),
                        flashcardDao = database.flashcardDao()
                    )
                ) as T
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
