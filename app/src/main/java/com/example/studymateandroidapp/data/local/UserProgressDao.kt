package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProgress(progress: UserProgress)

    @Transaction
    suspend fun addXp(amount: Int) {
        val currentProgress = getUserProgressSync() ?: UserProgress()
        val newProgress = currentProgress.copy(totalXp = currentProgress.totalXp + amount)
        saveUserProgress(newProgress)
        android.util.Log.d("XP", "Awarded $amount XP. Total XP: ${newProgress.totalXp}")
    }

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgressSync(): UserProgress?
}
