package com.example.studymateandroidapp.feature.motivation.data

import androidx.room.*
import com.example.studymateandroidapp.core.model.Achievement
import com.example.studymateandroidapp.core.model.AchievementType
import com.example.studymateandroidapp.core.model.DailyReflection
import kotlinx.coroutines.flow.Flow

@Dao
interface MotivationDao {

    // ── Daily Reflections ─────────────────────────────────

    @Query("SELECT * FROM daily_reflections WHERE date = :epochDay")
    suspend fun getReflectionForDate(epochDay: Long): DailyReflection?

    @Query("SELECT * FROM daily_reflections ORDER BY date DESC")
    fun getAllReflections(): Flow<List<DailyReflection>>

    @Query("SELECT * FROM daily_reflections")
    suspend fun getAllReflectionsList(): List<DailyReflection>

    @Query("SELECT * FROM daily_reflections ORDER BY date DESC LIMIT :limit")
    fun getRecentReflections(limit: Int): Flow<List<DailyReflection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: DailyReflection): Long

    @Update
    suspend fun updateReflection(reflection: DailyReflection)

    @Delete
    suspend fun deleteReflection(reflection: DailyReflection)

    // ── Achievements ──────────────────────────────────────

    @Query("SELECT * FROM achievements ORDER BY unlockedAt DESC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements")
    suspend fun getAllAchievementsList(): List<Achievement>

    @Query("SELECT * FROM achievements WHERE type = :type LIMIT 1")
    suspend fun getAchievementByType(type: AchievementType): Achievement?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: Achievement): Long

    @Query("SELECT COUNT(*) FROM achievements")
    fun getAchievementCount(): Flow<Int>
}
