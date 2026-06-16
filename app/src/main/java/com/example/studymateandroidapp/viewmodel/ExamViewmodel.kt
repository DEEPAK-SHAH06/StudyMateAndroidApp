package com.example.studymateandroidapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.StudyProgress
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import com.example.studymateandroidapp.data.repository.ExamRepository
import com.example.studymateandroidapp.data.repository.StudyProgressRepository
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import java.time.Instant
import java.time.ZoneId

/**
 * ViewModel responsible for managing exam-related data and logic.
 *
 * This includes providing a list of all exams, tracking study progress, 
 * and handling CRUD operations for exams, including scheduling notifications
 * and updating widgets.
 *
 * @property repository The repository for exam data.
 * @property studyProgressRepository The repository for study progress data.
 * @property reminderScheduler Helper for scheduling exam reminders.
 * @property application The application context, used for widget updates.
 */
class ExamViewmodel(
    private val repository: ExamRepository,
    private val studyProgressRepository: StudyProgressRepository,
    private val reminderScheduler: ReminderScheduler,
    private val application: Application
) : ViewModel() {

    /**
     * A [StateFlow] emitting the list of all available [Exam]s.
     */
    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * A [StateFlow] emitting a map of exam IDs to their corresponding [StudyProgress].
     */
    val examProgress: StateFlow<Map<Long, StudyProgress>> = studyProgressRepository.getAllProgress()
        .map { list -> list.associateBy { it.examId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /**
     * Retrieves an [ExamWithDetails] by its ID.
     *
     * @param id The ID of the exam.
     * @return A flow emitting the exam details.
     */
    fun getExamWithDetails(id: Long) = repository.getExamWithDetails(id)

    /**
     * Adds a new exam to the database, schedules reminders, and updates widgets.
     *
     * @param exam The [Exam] to add.
     */
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

    /**
     * Updates an existing exam in the database, reschedules reminders, and updates widgets.
     *
     * @param exam The [Exam] to update.
     */
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

    /**
     * Deletes an exam from the database, cancels its reminders, and updates widgets.
     *
     * @param exam The [Exam] to delete.
     */
    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.delete(exam)
            reminderScheduler.cancelExamReminders(exam.id)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }
}
