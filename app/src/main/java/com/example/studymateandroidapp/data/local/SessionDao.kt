package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.StudySession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface SessionDao {

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions")
    suspend fun getAllSessionsList(): List<StudySession>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    fun getSessionById(id: Long): Flow<StudySession?>

    @Query("""
        SELECT * FROM study_sessions
        WHERE startTime BETWEEN :startOfDay AND :endOfDay
        ORDER BY startTime DESC
    """)
    fun getSessionsForDay(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<List<StudySession>>

    @Query("""
        SELECT * FROM study_sessions
        WHERE taskId = :taskId
        ORDER BY startTime DESC
    """)
    fun getSessionsForTask(taskId: Long): Flow<List<StudySession>>

    @Query("SELECT startTime FROM study_sessions")
    fun getAllStartTimes(): Flow<List<LocalDateTime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: StudySession): Long

    @Update
    suspend fun update(session: StudySession)

    @Delete
    suspend fun delete(session: StudySession)

    // ── Statistics Queries ───────────────────────────────

    @Query("""
        SELECT COALESCE(SUM(durationSeconds), 0)
        FROM study_sessions
    """)
    fun getTotalStudySeconds(): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(durationSeconds), 0)
        FROM study_sessions
        WHERE startTime BETWEEN :startOfDay AND :endOfDay
    """)
    fun getStudySecondsForDay(
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(durationSeconds), 0)
        FROM study_sessions
        WHERE startTime >= :dateTime
    """)
    fun getStudySecondsSince(dateTime: LocalDateTime): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_sessions WHERE isCompleted = 1")
    fun getCompletedPomodoroCount(): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(durationSeconds), 0)
        FROM study_sessions
        WHERE examId = :examId
    """)
    fun getStudySecondsForExam(examId: Long): Flow<Int>
}