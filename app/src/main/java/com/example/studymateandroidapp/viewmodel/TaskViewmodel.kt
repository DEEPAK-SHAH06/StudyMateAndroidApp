package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.studymateandroidapp.utils.notification.ReminderScheduler

class TaskViewmodel(
    private val repository: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(task: Task) {
        viewModelScope.launch {
            val id = repository.insert(task)
            if (task.dueDate != null) {
                reminderScheduler.scheduleTaskReminder(
                    taskId = id,
                    title = task.title,
                    dueDate = task.dueDate,
                    reminderTime = task.dueTime ?: java.time.LocalTime.of(9, 0)
                )
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
            if (task.dueDate != null) {
                reminderScheduler.scheduleTaskReminder(
                    taskId = task.id,
                    title = task.title,
                    dueDate = task.dueDate,
                    reminderTime = task.dueTime ?: java.time.LocalTime.of(9, 0)
                )
            } else {
                reminderScheduler.cancelTaskReminder(task.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            reminderScheduler.cancelTaskReminder(task.id)
        }
    }

    suspend fun getTaskById(id: Long): Task? = repository.getTaskById(id)
}
