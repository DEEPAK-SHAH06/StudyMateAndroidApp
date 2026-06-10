package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.repository.TaskRepository
import com.example.studymateandroidapp.data.repository.MotivationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.studymateandroidapp.utils.notification.ReminderScheduler

class TaskViewmodel(
    private val repository: TaskRepository,
    private val motivationRepository: MotivationRepository,
    private val reminderScheduler: ReminderScheduler
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
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
            if (task.dueDate != null && task.dueTime != null) {
                reminderScheduler.scheduleTaskReminders(
                    taskId = task.id,
                    title = task.title,
                    dueDate = task.dueDate,
                    dueTime = task.dueTime
                )
            } else {
                reminderScheduler.cancelTaskReminders(task.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            reminderScheduler.cancelTaskReminders(task.id)
        }
    }

    fun onTaskCompletionToggled(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                repository.update(task.copy(
                    isCompleted = completed,
                    status = if (completed) com.example.studymateandroidapp.data.model.TaskStatus.COMPLETED else com.example.studymateandroidapp.data.model.TaskStatus.TODO,
                    completedAt = if (completed) java.time.LocalDate.now() else null
                ))
                if (completed) {
                    motivationRepository.recordStudyActivity()
                }
            }
        }
    }

    suspend fun getTaskById(id: Long): Task? = repository.getTaskById(id)
}
