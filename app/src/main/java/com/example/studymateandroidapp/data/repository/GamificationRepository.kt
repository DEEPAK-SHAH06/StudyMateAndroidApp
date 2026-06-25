package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.UserProgressDao
import com.example.studymateandroidapp.data.model.CelebrationEvent
import com.example.studymateandroidapp.data.model.CelebrationType
import com.example.studymateandroidapp.data.model.LevelInfo
import com.example.studymateandroidapp.data.model.UserProgress
import com.example.studymateandroidapp.data.model.XpEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GamificationRepository(private val userProgressDao: UserProgressDao) {
    
    private val _xpEvents = MutableSharedFlow<XpEvent>()
    val xpEvents = _xpEvents.asSharedFlow()

    private val _celebrationEvents = MutableSharedFlow<CelebrationEvent>()
    val celebrationEvents = _celebrationEvents.asSharedFlow()

    fun getUserProgress(): Flow<UserProgress?> = userProgressDao.getUserProgress()

    suspend fun addXp(amount: Int, message: String) {
        val oldXp = getTotalXpSync()
        userProgressDao.addXp(amount)
        val newXp = oldXp + amount
        
        _xpEvents.emit(XpEvent(amount, message))

        // Detect level up
        val oldLevel = calculateLevelInfo(oldXp).level
        val newLevelInfo = calculateLevelInfo(newXp)
        if (newLevelInfo.level > oldLevel) {
            triggerCelebration(
                CelebrationEvent(
                    type = CelebrationType.LEVEL_UP,
                    title = "Level Up!",
                    subtitle = "Reached Level ${newLevelInfo.level}",
                    icon = "⭐"
                )
            )
        }
    }

    suspend fun triggerCelebration(event: CelebrationEvent) {
        _celebrationEvents.emit(event)
    }

    suspend fun getTotalXpSync(): Int {
        return userProgressDao.getUserProgressSync()?.totalXp ?: 0
    }

    fun getLevelInfoFlow(): Flow<LevelInfo> {
        return getUserProgress().map { progress ->
            calculateLevelInfo(progress?.totalXp ?: 0)
        }
    }

    private fun calculateLevelInfo(totalXp: Int): LevelInfo {
        var level = 1
        var remainingXp = totalXp
        var xpForNext = 100 // XP needed to go from level 1 to 2

        while (remainingXp >= xpForNext) {
            remainingXp -= xpForNext
            level++
            xpForNext = 100 + (level - 1) * 150
        }

        val progress = if (xpForNext > 0) remainingXp.toFloat() / xpForNext else 0f
        
        val title = when {
            level >= 20 -> "Study Master 🏆"
            level >= 10 -> "Advanced Learner 🔥"
            level >= 5 -> "Scholar 🎓"
            else -> "Beginner 🌱"
        }

        return LevelInfo(
            totalXp = totalXp,
            level = level,
            currentLevelXp = remainingXp,
            xpToNextLevel = xpForNext,
            progress = progress,
            title = title
        )
    }
}
