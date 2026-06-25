package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.model.GoalStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Cross-cutting repository that aggregates task, session and goal data
 * to power the Statistics screen.
 */
class StatisticsRepository(
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val goalRepository: GoalRepository,
    private val motivationRepository: MotivationRepository,
    private val gamificationRepository: GamificationRepository
) {

    // ── Aggregate model ───────────────────────────────────

    fun getLevelInfo() = gamificationRepository.getLevelInfoFlow()
    fun getXpEvents() = gamificationRepository.xpEvents

    data class OverviewStats(
        val totalTasks: Int,
        val completedTasks: Int,
        val overdueTasks: Int,
        val totalStudySeconds: Int,
        val completedGoals: Int,
        val overdueGoals: Int
    ) {
        val taskCompletionRate: Int
            get() = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0

        val formattedStudyTime: String
            get() = formatDuration(totalStudySeconds)
    }

    data class DailyStudyData(
        val date: LocalDate,
        val studySeconds: Int
    )

    data class BestStudyDay(
        val date: LocalDate,
        val seconds: Int
    )

    // ── Flows ─────────────────────────────────────────────

    fun getOverviewStats(): Flow<OverviewStats> {
        val today = LocalDate.now()
        val now = java.time.LocalTime.now()
        
        return combine(
            taskRepository.getTotalCount(),
            taskRepository.getCompletedCount(),
            taskRepository.getOverdueCount(today, now),
            sessionRepository.getTotalStudySeconds(),
            goalRepository.getAllGoals(),
            goalRepository.getOverdueCount(today)
        ) { args: Array<Any> ->
            val total = args[0] as Int
            val completed = args[1] as Int
            val overdueTasks = args[2] as Int
            val seconds = args[3] as Int
            val goals = args[4] as List<com.example.studymateandroidapp.data.model.Goal>
            val overdueGoals = args[5] as Int
            
            val completedGoalsCount = goals.count { it.status == GoalStatus.COMPLETED }
            
            OverviewStats(
                totalTasks        = total,
                completedTasks    = completed,
                overdueTasks      = overdueTasks,
                totalStudySeconds = seconds,
                completedGoals    = completedGoalsCount,
                overdueGoals      = overdueGoals
            )
        }
    }

    fun getTodayStudySeconds(): Flow<Int> =
        sessionRepository.getStudySecondsForDate(LocalDate.now())

    fun getThisWeekStudySeconds(): Flow<Int> {
        val weekStart = LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            .atStartOfDay()
        return sessionRepository.getStudySecondsSince(weekStart)
    }

    fun getStreak(): Flow<Int> = motivationRepository.getStreak()
    fun getBestStreak(): Flow<Int> = motivationRepository.getBestStreak()
    fun getWeeklyStreakStatus(): Flow<List<Boolean>> = motivationRepository.getWeeklyStreakStatus()

    fun getDailyStudyData(days: Int = 7): Flow<List<DailyStudyData>> {
        val today = LocalDate.now()
        val flows = (0 until days).map { offset ->
            val date = today.minusDays(offset.toLong())
            sessionRepository.getStudySecondsForDate(date).map { seconds ->
                DailyStudyData(date, seconds)
            }
        }
        return combine(flows) { dailyDataArray ->
            dailyDataArray.filterIsInstance<DailyStudyData>().reversed()
        }
    }

    fun getBestStudyDayThisWeek(): Flow<BestStudyDay?> {
        val weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val flows = (0 until 7).map { offset ->
            val date = weekStart.plusDays(offset.toLong())
            sessionRepository.getStudySecondsForDate(date).map { seconds ->
                BestStudyDay(date, seconds)
            }
        }
        return combine(flows) { days ->
            val daysList = days.filterIsInstance<BestStudyDay>()
            daysList.maxByOrNull { it.seconds }?.takeIf { it.seconds > 0 }
        }
    }

    companion object {
        fun formatDuration(totalSeconds: Int): String {
            val h = totalSeconds / 3600
            val m = (totalSeconds % 3600) / 60
            val s = totalSeconds % 60
            
            return when {
                h > 0 -> "${h}h ${m}m ${s}s"
                m > 0 -> "${m}m ${s}s"
                else -> "${s}s"
            }
        }
    }
}
