package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.StudyProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyProgressDao {
    @Query("SELECT * FROM study_progress WHERE examId = :examId")
    fun getProgressByExamId(examId: Long): Flow<StudyProgress?>

    @Query("SELECT * FROM study_progress")
    fun getAllProgress(): Flow<List<StudyProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: StudyProgress): Long

    @Query("UPDATE study_progress SET totalStudyTime = totalStudyTime + :addedTime, lastStudiedTimestamp = :timestamp, lastUpdated = :lastUpdated WHERE examId = :examId")
    suspend fun addStudyTime(examId: Long, addedTime: Long, timestamp: Long = System.currentTimeMillis(), lastUpdated: Long = System.currentTimeMillis())

    @Query("UPDATE study_progress SET completionPercentage = :percentage, lastUpdated = :lastUpdated WHERE examId = :examId")
    suspend fun updateCompletion(examId: Long, percentage: Float, lastUpdated: Long = System.currentTimeMillis())

    @Query("SELECT * FROM study_progress")
    suspend fun getAllProgressList(): List<StudyProgress>

    @Update
    suspend fun update(progress: StudyProgress)
}
