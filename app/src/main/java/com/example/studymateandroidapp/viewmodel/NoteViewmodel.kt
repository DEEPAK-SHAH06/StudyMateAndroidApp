package com.example.studymateandroidapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.data.repository.NoteRepository
import com.example.studymateandroidapp.data.repository.ExamRepository
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewmodel(
    private val repository: NoteRepository,
    private val examRepository: ExamRepository,
    private val application: Application
) : ViewModel() {

    val allExams = examRepository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getNotesByExamId(examId: Long): StateFlow<List<Note>> =
        repository.getNotesByExamId(examId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getNoteById(id: Long): Note? = repository.getNoteById(id)

    fun addNote(note: Note) {
        viewModelScope.launch {
            val examExists = examRepository.getExamById(note.examId).firstOrNull() != null
            if (examExists) {
                android.util.Log.d("NoteCrashFix", "Inserting note: $note")
                repository.insert(note)
                WidgetUpdateHelper.updateAllWidgets(application)
            } else {
                android.util.Log.e("NoteCrashFix", "Failed to insert note: Exam with ID ${note.examId} does not exist.")
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            val examExists = examRepository.getExamById(note.examId).firstOrNull() != null
            if (examExists) {
                repository.update(note)
                WidgetUpdateHelper.updateAllWidgets(application)
            } else {
                android.util.Log.e("NoteCrashFix", "Failed to update note: Exam with ID ${note.examId} does not exist.")
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }
}
