package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.repository.ExamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamViewmodel(private val repository: ExamRepository) : ViewModel() {

    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExam(exam: Exam) {
        viewModelScope.launch {
            repository.insert(exam)
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch {
            repository.update(exam)
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.delete(exam)
        }
    }
}
