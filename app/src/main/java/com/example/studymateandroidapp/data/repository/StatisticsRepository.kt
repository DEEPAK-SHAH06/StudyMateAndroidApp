package com.example.studymateandroidapp.data.repository

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
        val totalStudyMinutes: Int,
        val completedGoals: Int
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

    fun getOverviewStats(): Flow<OverviewStats> = combine(
        taskRepository.getTotalCount(),
        taskRepository.getCompletedCount(),
        sessionRepository.getTotalStudyMinutes(),
        goalRepository.completedGoalCount()
    ) { total, completed, minutes, goals ->
        OverviewStats(
            totalTasks        = total,
            completedTasks    = completed,
            totalStudyMinutes = minutes,
            completedGoals    = goals
        )
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