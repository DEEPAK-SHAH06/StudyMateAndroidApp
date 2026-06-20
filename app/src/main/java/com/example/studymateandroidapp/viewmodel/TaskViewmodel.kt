package com.example.studymateandroidapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.model.CelebrationEvent
import com.example.studymateandroidapp.data.model.CelebrationType
import com.example.studymateandroidapp.data.repository.MotivationRepository
import com.example.studymateandroidapp.data.repository.TaskRepository
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewmodel(
    private val repository: TaskRepository,
    private val motivationRepository: MotivationRepository,
    private val reminderScheduler: ReminderScheduler,
    private val application: Application
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(task: Task) {
        viewModelScope.launch {
            val id = repository.insert(task)
            if (task.dueDate != null && task.dueTime != null) {
                reminderScheduler.scheduleTaskReminders(
                    taskId = id,
                    title = task.title,
                    dueDate = task.dueDate,
                    dueTime = task.dueTime
                )
            }
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = if (task.isCompleted && !task.isXpAwarded) {
                motivationRepository.addXp(5, "Task Completed!")
                motivationRepository.triggerCelebration(
                    CelebrationEvent(
                        type = CelebrationType.TASK_COMPLETED,
                        title = "Task Completed",
                        subtitle = task.title,
                        xpReward = 5,
                        icon = "✅"
                    )
                )
                task.copy(isXpAwarded = true)
            } else {
                task
            }
            
            repository.update(updatedTask)
            if (updatedTask.dueDate != null && updatedTask.dueTime != null) {
                reminderScheduler.scheduleTaskReminders(
                    taskId = updatedTask.id,
                    title = updatedTask.title,
                    dueDate = updatedTask.dueDate,
                    dueTime = updatedTask.dueTime
                )
            } else {
                reminderScheduler.cancelTaskReminders(updatedTask.id)
            }
            WidgetUpdateHelper.updateAllWidgets(application)
            if (updatedTask.isCompleted) {
                motivationRepository.checkAndUnlockAchievements()
            }
        }
    }

    fun deleteTask(task: Task, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.delete(task)
            reminderScheduler.cancelTaskReminders(task.id)
            WidgetUpdateHelper.updateAllWidgets(application)
            if (task.isCompleted) {
                motivationRepository.checkAndUnlockAchievements()
            }
            onComplete()
        }
    }

    suspend fun getTaskById(id: Long): Task? = repository.getTaskById(id)
}
