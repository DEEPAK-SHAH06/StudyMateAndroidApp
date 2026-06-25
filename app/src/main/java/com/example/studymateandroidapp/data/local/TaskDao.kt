package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksList(): List<Task>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    fun getPendingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date")
    fun getTasksDueOn(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date AND isCompleted = 0")
    fun getIncompleteTasksDueOn(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND (dueDate < :date OR (dueDate = :date AND dueTime < :time))")
    fun getOverdueCount(date: LocalDate, time: java.time.LocalTime): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): Flow<Int>

    @Query("SELECT DISTINCT completedAt FROM tasks WHERE isCompleted = 1 AND completedAt IS NOT NULL")
    fun getCompletedDates(): Flow<List<LocalDate>>

    @Query("UPDATE tasks SET isPinned = 1, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun pinTask(id: Long, lastUpdated: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isPinned = 0, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun unpinTask(id: Long, lastUpdated: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isPinned = NOT isPinned, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun togglePinned(id: Long, lastUpdated: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
