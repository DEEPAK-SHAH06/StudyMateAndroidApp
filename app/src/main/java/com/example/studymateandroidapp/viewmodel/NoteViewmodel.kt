package com.example.studymateandroidapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.data.repository.NoteRepository
import com.example.studymateandroidapp.ui.widget.WidgetUpdateHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewmodel(
    private val repository: NoteRepository,
    private val application: Application
) : ViewModel() {

    val allNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getNotesByExamId(examId: Long): StateFlow<List<Note>> =
        repository.getNotesByExamId(examId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getNoteById(id: Long): Note? = repository.getNoteById(id)

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            WidgetUpdateHelper.updateAllWidgets(application)
        }
    }
}
