package com.example.studymateandroidapp.data.repository

import android.content.Context
import com.example.studymateandroidapp.data.local.GoalDao
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GoalRepository(
    private val goalDao: GoalDao,
    private val context: Context
) {

    private suspend fun triggerWidgetUpdate() {
        WidgetUpdateHelper.updateAllWidgets(context)
    }

    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    fun getGoalById(id: Long): Flow<Goal?> = goalDao.getGoalById(id)

    fun getActiveGoals(): Flow<List<Goal>> = goalDao.getActiveGoals()

    fun getGoalsForExam(examId: Long): Flow<List<Goal>> = goalDao.getGoalsForExam(examId)

    suspend fun insert(goal: Goal): Long {
        val id = goalDao.insert(goal)
        triggerWidgetUpdate()
        return id
    }

    suspend fun update(goal: Goal) {
        goalDao.update(goal.copy(lastUpdated = System.currentTimeMillis()))
        triggerWidgetUpdate()
    }

    suspend fun updateProgress(id: Long, value: Int) {
        goalDao.updateProgress(id, value)
        triggerWidgetUpdate()
    }

    suspend fun delete(goal: Goal) {
        goalDao.delete(goal)
        triggerWidgetUpdate()
    }

    suspend fun deleteById(id: Long) {
        goalDao.deleteById(id)
        triggerWidgetUpdate()
    }

    fun getCompletedGoalCount(): Flow<Int> = goalDao.getCompletedGoalCount()

    fun getOverdueCount(date: LocalDate): Flow<Int> = goalDao.getOverdueCount(date)
}
