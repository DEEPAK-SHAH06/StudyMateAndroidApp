package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.model.Achievement
import com.example.studymateandroidapp.data.model.AchievementType
import com.example.studymateandroidapp.data.model.DailyReflection
import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.local.GoalDao
import com.example.studymateandroidapp.data.local.NoteDao
import com.example.studymateandroidapp.data.local.FlashcardDao
import com.example.studymateandroidapp.data.local.MotivationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class MotivationRepository(
    private val motivationDao: MotivationDao,
    private val taskDao: TaskDao,
    private val goalDao: GoalDao,
    private val noteDao: NoteDao,
    private val flashcardDao: FlashcardDao
) {
    // ── Reflections ───────────────────────────────────────

    suspend fun getReflectionForDate(date: LocalDate): DailyReflection? =
        motivationDao.getReflectionForDate(date.toEpochDay())

    fun getAllReflections(): Flow<List<DailyReflection>> =
        motivationDao.getAllReflections()

    fun getRecentReflections(limit: Int = 7): Flow<List<DailyReflection>> =
        motivationDao.getRecentReflections(limit)

    suspend fun saveReflection(reflection: DailyReflection) {
        val existing = motivationDao.getReflectionForDate(reflection.date)
        if (existing != null) {
            motivationDao.updateReflection(
                reflection.copy(
                    id = existing.id,
                    createdAt = existing.createdAt,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        } else {
            motivationDao.insertReflection(reflection.copy(lastUpdated = System.currentTimeMillis()))
        }
    }

    suspend fun deleteReflection(reflection: DailyReflection) {
        motivationDao.deleteReflection(reflection)
    }

    // ── Achievements ──────────────────────────────────────

    fun getAllAchievements(): Flow<List<Achievement>> =
        motivationDao.getAllAchievements()

    fun getAchievementCount(): Flow<Int> =
        motivationDao.getAchievementCount()

    suspend fun hasAchievement(type: AchievementType): Boolean =
        motivationDao.getAchievementByType(type) != null

    /**
     * Checks all achievement conditions and unlocks any new ones.
     * Returns list of newly unlocked achievements.
     */
    suspend fun checkAndUnlockAchievements(): List<Achievement> {
        val newAchievements = mutableListOf<Achievement>()

        // Task achievements
        val completedTasks = taskDao.getCompletedCount().firstOrNull() ?: 0
        if (completedTasks >= 1) {
            tryUnlock(AchievementType.FIRST_TASK, "First Task!", "Completed your first task")?.let { newAchievements.add(it) }
        }
        if (completedTasks >= 10) {
            tryUnlock(AchievementType.TEN_TASKS, "Task Master", "Completed 10 tasks")?.let { newAchievements.add(it) }
        }
        if (completedTasks >= 50) {
            tryUnlock(AchievementType.FIFTY_TASKS, "Task Legend", "Completed 50 tasks")?.let { newAchievements.add(it) }
        }

        // Note achievements
        val noteCount = noteDao.getNoteCount().firstOrNull() ?: 0
        if (noteCount >= 1) {
            tryUnlock(AchievementType.FIRST_NOTE, "Note Taker", "Created your first note")?.let { newAchievements.add(it) }
        }
        if (noteCount >= 10) {
            tryUnlock(AchievementType.TEN_NOTES, "Scribe", "Created 10 notes")?.let { newAchievements.add(it) }
        }

        // Goal achievements
        val completedGoals = goalDao.getCompletedGoalCount().firstOrNull() ?: 0
        if (completedGoals >= 1) {
            tryUnlock(AchievementType.FIRST_GOAL_COMPLETE, "Goal Getter", "Completed your first goal")?.let { newAchievements.add(it) }
        }
        if (completedGoals >= 5) {
            tryUnlock(AchievementType.FIVE_GOALS_COMPLETE, "Ambitious", "Completed 5 goals")?.let { newAchievements.add(it) }
        }

        // Flashcard achievements
        val flashcardCount = flashcardDao.getFlashcardCount().firstOrNull() ?: 0
        if (flashcardCount >= 1) {
            tryUnlock(AchievementType.FIRST_FLASHCARD, "Flash Scholar", "Created your first flashcard")?.let { newAchievements.add(it) }
        }

        // Reflection achievements
        val todayReflection = motivationDao.getReflectionForDate(LocalDate.now().toEpochDay())
        if (todayReflection != null) {
            tryUnlock(AchievementType.FIRST_REFLECTION, "Self Aware", "Wrote your first daily reflection")?.let { newAchievements.add(it) }
        }

        // Streak achievements - check consecutive days with completed tasks
        val streak = calculateStreak()
        if (streak >= 7) {
            tryUnlock(AchievementType.SEVEN_DAY_STREAK, "Week Warrior", "7-day study streak!")?.let { newAchievements.add(it) }
        }
        if (streak >= 14) {
            tryUnlock(AchievementType.FOURTEEN_DAY_STREAK, "Fortnight Focus", "14-day study streak!")?.let { newAchievements.add(it) }
        }
        if (streak >= 30) {
            tryUnlock(AchievementType.THIRTY_DAY_STREAK, "Monthly Master", "30-day study streak!")?.let { newAchievements.add(it) }
        }

        return newAchievements
    }

    private suspend fun tryUnlock(type: AchievementType, title: String, description: String): Achievement? {
        if (hasAchievement(type)) return null
        val achievement = Achievement(
            type = type,
            title = title,
            description = description
        )
        motivationDao.insertAchievement(achievement)
        return achievement
    }

    /**
     * Calculate consecutive days with at least one completed task.
     * Counts backwards from today.
     */
    private suspend fun calculateStreak(): Int {
        var streak = 0
        var date = LocalDate.now()
        
        for (i in 0 until 60) { // Check up to 60 days back
            val tasks = taskDao.getTasksDueOn(date).firstOrNull() ?: emptyList()
            if (tasks.any { it.isCompleted }) {
                streak++
                date = date.minusDays(1)
            } else {
                break
            }
        }
        
        return streak
    }

    /**
     * Encouragement messages for goal completion.
     */
    fun getEncouragementMessage(): String {
        val messages = listOf(
            "🎯 Amazing work! You crushed that goal!",
            "🌟 You're on fire! Keep up the incredible momentum!",
            "💪 Goal achieved! Your dedication is inspiring!",
            "🏆 Champion move! Another goal conquered!",
            "🚀 You're unstoppable! What's next?",
            "✨ Brilliant! Your hard work is paying off!",
            "🎉 Celebration time! You earned this!",
            "💎 Pure excellence! You make it look easy!",
            "🔥 Goals fear you! Another one down!",
            "⭐ Star student! Your focus is remarkable!"
        )
        return messages.random()
    }
}
