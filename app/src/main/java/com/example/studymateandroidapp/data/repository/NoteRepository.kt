package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.NoteDao
import com.example.studymateandroidapp.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun getNotesByExamId(examId: Long): Flow<List<Note>> =
        noteDao.getNotesByExamId(examId)

    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }
}
