package com.example.studymateandroidapp.feature.goal.data

import com.example.studymateandroidapp.core.model.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()
    val completedGoalCount: Flow<Int> = goalDao.getCompletedGoalCount()

    suspend fun insert(goal: Goal) {
        goalDao.insert(goal)
    }

    suspend fun update(goal: Goal) {
        goalDao.update(goal)
    }

    suspend fun delete(goal: Goal) {
        goalDao.delete(goal)
    }
}
