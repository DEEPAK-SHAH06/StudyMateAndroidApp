package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.SessionDao
import com.example.studymateandroidapp.data.model.StudySession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SessionRepository(private val sessionDao: SessionDao) {

    fun getAllSessions(): Flow<List<StudySession>> =
        sessionDao.getAllSessions()

    fun getSessionById(id: Long): Flow<StudySession?> =
        sessionDao.getSessionById(id)

    fun getSessionsForDate(date: LocalDate): Flow<List<StudySession>> =
        sessionDao.getSessionsForDay(
            startOfDay = date.atStartOfDay(),
            endOfDay   = date.atTime(LocalTime.MAX)
        )

    fun getSessionsForTask(taskId: Long): Flow<List<StudySession>> =
        sessionDao.getSessionsForTask(taskId)

    suspend fun insert(session: StudySession): Long =
        sessionDao.insert(session)

    suspend fun update(session: StudySession) =
        sessionDao.update(session)

    suspend fun delete(session: StudySession) =
        sessionDao.delete(session)

    // ── Statistics (used by StatisticsRepository) ─────────

    fun getTotalStudyMinutes(): Flow<Int> =
        sessionDao.getTotalStudyMinutes()

    fun getStudyMinutesForDate(date: LocalDate): Flow<Int> =
        sessionDao.getStudyMinutesForDay(
            startOfDay = date.atStartOfDay(),
            endOfDay   = date.atTime(LocalTime.MAX)
        )

    fun getStudyMinutesSince(dateTime: LocalDateTime): Flow<Int> =
        sessionDao.getStudyMinutesSince(dateTime)
}