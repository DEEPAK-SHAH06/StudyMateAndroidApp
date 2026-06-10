package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.GoalDao
import com.example.studymateandroidapp.data.model.Goal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GoalRepository(private val goalDao: GoalDao) {

    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    fun getGoalById(id: Long): Flow<Goal?> = goalDao.getGoalById(id)

    fun getActiveGoals(): Flow<List<Goal>> = goalDao.getActiveGoals()

    fun getGoalsForExam(examId: Long): Flow<List<Goal>> = goalDao.getGoalsForExam(examId)

    suspend fun insert(goal: Goal): Long = goalDao.insert(goal)

    suspend fun update(goal: Goal) = goalDao.update(goal)

    suspend fun updateProgress(id: Long, value: Int) = goalDao.updateProgress(id, value)

    suspend fun delete(goal: Goal) = goalDao.delete(goal)

    suspend fun deleteById(id: Long) = goalDao.deleteById(id)

    fun getCompletedGoalCount(): Flow<Int> = goalDao.getCompletedGoalCount()

    fun getOverdueCount(date: LocalDate): Flow<Int> = goalDao.getOverdueCount(date)
}
