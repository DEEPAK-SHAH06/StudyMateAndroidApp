package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.StudyProgressDao
import com.example.studymateandroidapp.data.model.StudyProgress
import kotlinx.coroutines.flow.Flow

class StudyProgressRepository(private val studyProgressDao: StudyProgressDao) {
    fun getProgressByExamId(examId: Long): Flow<StudyProgress?> =
        studyProgressDao.getProgressByExamId(examId)

    fun getAllProgress(): Flow<List<StudyProgress>> =
        studyProgressDao.getAllProgress()

    suspend fun updateProgress(progress: StudyProgress) {
        studyProgressDao.insertOrUpdate(progress)
    }

    suspend fun addStudyTime(examId: Long, addedTimeMs: Long) {
        studyProgressDao.addStudyTime(examId, addedTimeMs)
    }

    suspend fun updateCompletion(examId: Long, percentage: Float) {
        studyProgressDao.updateCompletion(examId, percentage)
    }
}
