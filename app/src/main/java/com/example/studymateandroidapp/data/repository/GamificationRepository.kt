package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.UserProgressDao
import com.example.studymateandroidapp.data.model.LevelInfo
import com.example.studymateandroidapp.data.model.UserProgress
import com.example.studymateandroidapp.data.model.XpEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class GamificationRepository(private val userProgressDao: UserProgressDao) {
    
    private val _xpEvents = MutableSharedFlow<XpEvent>()
    val xpEvents = _xpEvents.asSharedFlow()

    fun getUserProgress(): Flow<UserProgress?> = userProgressDao.getUserProgress()

    suspend fun addXp(amount: Int, message: String) {
        userProgressDao.addXp(amount)
        _xpEvents.emit(XpEvent(amount, message))
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
