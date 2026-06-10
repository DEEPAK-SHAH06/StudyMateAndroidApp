package com.example.studymateandroidapp.data.local

import androidx.room.*
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY examDate ASC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE id = :id")
    fun getExamById(id: Long): Flow<Exam?>

    @Transaction
    @Query("SELECT * FROM exams WHERE id = :id")
    fun getExamWithDetails(id: Long): Flow<ExamWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exam: Exam): Long

    @Update
    suspend fun update(exam: Exam)

    @Delete
    suspend fun delete(exam: Exam)
}

