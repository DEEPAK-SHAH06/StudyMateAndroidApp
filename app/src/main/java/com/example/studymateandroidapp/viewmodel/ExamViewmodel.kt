package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import com.example.studymateandroidapp.data.repository.ExamRepository
import com.example.studymateandroidapp.data.model.StudyProgress
import com.example.studymateandroidapp.data.repository.StudyProgressRepository
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class ExamViewmodel(
    private val repository: ExamRepository,
    private val studyProgressRepository: StudyProgressRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examProgress: StateFlow<Map<Long, StudyProgress>> = studyProgressRepository.getAllProgress()
        .map { list -> list.associateBy { it.examId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getExamWithDetails(id: Long) = repository.getExamWithDetails(id)

    fun updateMastery(examId: Long, flashcardPerformance: Float, studyTimeMs: Long) {
        viewModelScope.launch {
            studyProgressRepository.getProgressByExamId(examId).take(1).collect { current ->
                val progress = current ?: StudyProgress(examId = examId)
                val newStudyTime = progress.totalStudyTime + studyTimeMs
                
                // Mastery Logic: 70% flashcards, 30% time (target 5 hours for full time points)
                val timePoints = (newStudyTime.toFloat() / (5 * 60 * 60 * 1000)).coerceAtMost(1f) * 0.3f
                val performancePoints = flashcardPerformance.coerceIn(0f, 1f) * 0.7f
                val newPercentage = (timePoints + performancePoints).coerceIn(0f, 1f)

                studyProgressRepository.updateProgress(progress.copy(
                    totalStudyTime = newStudyTime,
                    completionPercentage = newPercentage,
                    lastStudiedTimestamp = System.currentTimeMillis()
                ))
            }
        }
    }

    fun addExam(exam: Exam) {
        viewModelScope.launch {
            val id = repository.insert(exam)
            val ldt = Instant.ofEpochMilli(exam.examDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            
            reminderScheduler.scheduleExamReminders(
                examId = id,
                title = exam.title,
                subject = exam.subject,
                examDateTime = ldt,
                isTimeSet = exam.isTimeSet
            )
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch {
            repository.update(exam)
            val ldt = Instant.ofEpochMilli(exam.examDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            
            reminderScheduler.scheduleExamReminders(
                examId = exam.id,
                title = exam.title,
                subject = exam.subject,
                examDateTime = ldt,
                isTimeSet = exam.isTimeSet
            )
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.delete(exam)
            reminderScheduler.cancelExamReminders(exam.id)
        }
    }
}
