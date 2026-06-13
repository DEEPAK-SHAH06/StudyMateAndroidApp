package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE examId = :examId ORDER BY createdAt DESC")
    fun getNotesByExamId(examId: Long): Flow<List<Note>>

    @Query("SELECT COUNT(*) FROM notes")
    fun getNoteCount(): Flow<Int>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
