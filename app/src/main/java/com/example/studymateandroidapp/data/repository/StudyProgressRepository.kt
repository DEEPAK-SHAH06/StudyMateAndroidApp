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
        studyProgressDao.insertOrUpdate(progress.copy(lastUpdated = System.currentTimeMillis()))
    }

    suspend fun addStudyTime(examId: Long, addedTimeMs: Long) {
        studyProgressDao.addStudyTime(examId, addedTimeMs)
    }

    suspend fun updateCompletion(examId: Long, percentage: Float) {
        studyProgressDao.updateCompletion(examId, percentage)
    }

    suspend fun refreshProgress(examId: Long) {
        val totalTimeSeconds = studyProgressDao.getTotalStudyTimeForExam(examId) ?: 0L
        val totalTimeMs = totalTimeSeconds * 1000
        
        val correct = studyProgressDao.getTotalCorrectFlashcardsForExam(examId) ?: 0
        val total = studyProgressDao.getTotalReviewedFlashcardsForExam(examId) ?: 0
        
        val mastery = if (total > 0) correct.toFloat() / total else 0f
        
        // Simple logic for completion: 50% based on study time (goal: 5 hours), 50% based on flashcard mastery
        val timeTargetMs = 5 * 3600 * 1000L 
        val timeProgress = (totalTimeMs.toFloat() / timeTargetMs).coerceIn(0f, 1f)
        
        val completion = (timeProgress * 0.5f) + (mastery * 0.5f)
        
        val existingProgress = studyProgressDao.getProgressByExamIdSync(examId)
        if (existingProgress != null) {
            studyProgressDao.insertOrUpdate(existingProgress.copy(
                totalStudyTime = totalTimeMs,
                flashcardMastery = mastery,
                completionPercentage = completion,
                lastStudiedTimestamp = System.currentTimeMillis()
            ))
        } else {
            studyProgressDao.insertOrUpdate(StudyProgress(
                examId = examId,
                totalStudyTime = totalTimeMs,
                flashcardMastery = mastery,
                completionPercentage = completion
            ))
        }
    }
}
