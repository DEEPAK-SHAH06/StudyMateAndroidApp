package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.ProgressDao
import com.example.studymateandroidapp.data.model.ProgressEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProgressRepository @Inject constructor(
    private val progressDao: ProgressDao
) {
    val allProgress: Flow<List<ProgressEntry>> = progressDao.getAll()

    fun getProgressBetween(start: java.time.LocalDate, end: java.time.LocalDate): Flow<List<ProgressEntry>> {
        return progressDao.getBetweenDates(start, end)
    }

    suspend fun insert(entry: ProgressEntry) {
        progressDao.insert(entry)
    }

    suspend fun update(entry: ProgressEntry) {
        progressDao.update(entry)
    }

    suspend fun delete(entry: ProgressEntry) {
        progressDao.delete(entry)
    }
}
