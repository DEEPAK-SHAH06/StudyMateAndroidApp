package com.example.studymateandroidapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import com.example.studymateandroidapp.data.repository.ExamRepository
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import java.time.Instant
import java.time.ZoneId

class ExamViewmodel(
    private val repository: ExamRepository,
    private val reminderScheduler: ReminderScheduler,
    private val application: Application
) : ViewModel() {

    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getExamWithDetails(id: Long) = repository.getExamWithDetails(id)

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
            WidgetUpdateHelper.updateAllWidgets(application)
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
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.delete(exam)
            reminderScheduler.cancelExamReminders(exam.id)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }
}
