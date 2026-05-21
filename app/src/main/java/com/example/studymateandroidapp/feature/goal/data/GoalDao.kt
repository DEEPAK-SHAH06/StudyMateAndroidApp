package com.example.studymateandroidapp.feature.goal.data

import androidx.room.*
import com.example.studymateandroidapp.core.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals")
    suspend fun getAllGoalsList(): List<Goal>

    @Query("SELECT COUNT(*) FROM goals WHERE isCompleted = 1")
    fun getCompletedGoalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)
}
