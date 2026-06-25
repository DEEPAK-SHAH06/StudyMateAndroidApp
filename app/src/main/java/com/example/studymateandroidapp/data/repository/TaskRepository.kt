package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    val pendingTasks: Flow<List<Task>> = taskDao.getPendingTasks()

    fun getTasksDueOn(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksDueOn(date)

    suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)

    suspend fun insert(task: Task): Long =
        taskDao.insert(task)

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    fun getCompletedDates(): Flow<List<LocalDate>> =
        taskDao.getCompletedDates()

    fun getCompletedCount(): Flow<Int> =
        taskDao.getCompletedCount()

    fun getOverdueCount(date: LocalDate, time: java.time.LocalTime): Flow<Int> =
        taskDao.getOverdueCount(date, time)

    fun getTotalCount(): Flow<Int> =
        taskDao.getTotalTaskCount()

    suspend fun pinTask(id: Long) = taskDao.pinTask(id)

    suspend fun unpinTask(id: Long) = taskDao.unpinTask(id)

    suspend fun togglePinned(id: Long) = taskDao.togglePinned(id)
}