package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.ExamDao
import com.example.studymateandroidapp.data.model.Exam
import kotlinx.coroutines.flow.Flow

class ExamRepository(private val examDao: ExamDao) {
    val allExams: Flow<List<Exam>> = examDao.getAllExams()

    suspend fun insert(exam: Exam) {
        examDao.insert(exam)
    }

    suspend fun update(exam: Exam) {
        examDao.update(exam)
    }

    suspend fun delete(exam: Exam) {
        examDao.delete(exam)
    }
}
