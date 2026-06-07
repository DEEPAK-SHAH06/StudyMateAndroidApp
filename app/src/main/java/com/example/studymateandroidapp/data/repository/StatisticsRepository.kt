package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.model.GoalStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Cross-cutting repository that aggregates task, session and goal data
 * to power the Statistics screen.
 *
 * Does NOT own a DAO — composes data from other repositories.
 */
class StatisticsRepository(
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val goalRepository: GoalRepository
) {

    // ── Aggregate model ───────────────────────────────────

    data class OverviewStats(
        val totalTasks: Int,
        val completedTasks: Int,
        val overdueTasks: Int,
        val totalStudyMinutes: Int,
        val completedGoals: Int,
        val overdueGoals: Int
    ) {
        val taskCompletionRate: Int
            get() = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0

        val formattedStudyTime: String
            get() = "${totalStudyMinutes / 60}h ${totalStudyMinutes % 60}m"
    }

    data class DailyStudyData(
        val date: LocalDate,
        val studyMinutes: Int
    )

    // ── Flows ─────────────────────────────────────────────

    fun getOverviewStats(): Flow<OverviewStats> {
        val today = LocalDate.now()
        val now = java.time.LocalTime.now()
        
        return combine(
            taskRepository.getTotalCount(),
            taskRepository.getCompletedCount(),
            taskRepository.getOverdueCount(today, now),
            sessionRepository.getTotalStudyMinutes(),
            goalRepository.allGoals,
            goalRepository.getOverdueCount(today)
        ) { args: Array<Any> ->
            val total = args[0] as Int
            val completed = args[1] as Int
            val overdueTasks = args[2] as Int
            val minutes = args[3] as Int
            val goals = args[4] as List<com.example.studymateandroidapp.data.model.Goal>
            val overdueGoals = args[5] as Int
            
            val completedGoalsCount = goals.count { it.status == GoalStatus.COMPLETED }
            
            OverviewStats(
                totalTasks        = total,
                completedTasks    = completed,
                overdueTasks      = overdueTasks,
                totalStudyMinutes = minutes,
                completedGoals    = completedGoalsCount,
                overdueGoals      = overdueGoals
            )
        }
    }

    fun getTodayStudyMinutes(): Flow<Int> =
        sessionRepository.getStudyMinutesForDate(LocalDate.now())

    fun getThisWeekStudyMinutes(): Flow<Int> {
        val weekStart = LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay()
        return sessionRepository.getStudyMinutesSince(weekStart)
    }

    fun getDailyStudyData(days: Int = 7): Flow<List<DailyStudyData>> {
        val today = LocalDate.now()
        val flows = (0 until days).map { offset ->
            sessionRepository.getStudyMinutesForDate(today.minusDays(offset.toLong()))
        }
        return combine(flows) { minutesArray ->
            minutesArray.mapIndexed { index, minutes ->
                DailyStudyData(
                    date         = today.minusDays(index.toLong()),
                    studyMinutes = minutes
                )
            }.reversed()
        }
    }
}
