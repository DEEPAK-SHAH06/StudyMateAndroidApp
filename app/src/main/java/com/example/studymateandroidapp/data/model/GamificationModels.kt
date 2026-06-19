package com.example.studymateandroidapp.data.model

/**
 * Detailed level progress for UI display.
 */
data class LevelInfo(
    val totalXp: Int,
    val level: Int,
    val currentLevelXp: Int,
    val xpToNextLevel: Int,
    val progress: Float,
    val title: String
)

/**
 * Event emitted when XP is earned.
 */
data class XpEvent(val amount: Int, val message: String)

/**
 * Progress towards a specific achievement.
 */
data class AchievementProgress(
    val type: AchievementType,
    val current: Int,
    val target: Int,
    val isUnlocked: Boolean
)

/**
 * Types of celebrations supported by the system.
 */
enum class CelebrationType {
    TASK_COMPLETED,
    ACHIEVEMENT_UNLOCKED,
    STREAK_REACHED,
    LEVEL_UP,
    GOAL_COMPLETED
}

/**
 * Metadata for a contextual celebration.
 */
data class CelebrationEvent(
    val type: CelebrationType,
    val title: String,
    val subtitle: String = "",
    val xpReward: Int? = null,
    val icon: String = "" // Emoji or icon identifier
)
