package com.example.studymateandroidapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studymateandroidapp.data.model.Goal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals")
    suspend fun getAllGoalsList(): List<Goal>

    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalById(id: Long): Flow<Goal?>

    @Query(
        """SELECT * FROM goals 
           WHERE status != 'COMPLETED' AND status != 'ABANDONED' 
           ORDER BY deadline ASC"""
    )
    fun getActiveGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE examId = :examId")
    fun getGoalsForExam(examId: Long): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Query(
        """UPDATE goals 
           SET currentValue = :value, 
               status = CASE WHEN :value >= targetValue THEN 'COMPLETED' ELSE status END,
               lastUpdated = :lastUpdated
           WHERE id = :id"""
    )
    suspend fun updateProgress(id: Long, value: Int, lastUpdated: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Long)

    // ── Statistics ────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM goals WHERE status = 'COMPLETED'")
    fun getCompletedGoalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals WHERE status != 'COMPLETED' AND status != 'ABANDONED' AND deadline < :date")
    fun getOverdueCount(date: LocalDate): Flow<Int>
}
