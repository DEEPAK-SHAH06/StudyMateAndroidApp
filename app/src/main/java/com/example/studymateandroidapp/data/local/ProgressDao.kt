package com.example.studymateandroidapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.studymateandroidapp.data.model.ProgressEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ProgressEntry)

    @Update
    suspend fun update(entry: ProgressEntry)

    @Delete
    suspend fun delete(entry: ProgressEntry)

    @Query("SELECT * FROM progress_entry ORDER BY date DESC")
    fun getAll(): Flow<List<ProgressEntry>>

    @Query("SELECT * FROM progress_entry WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getBetweenDates(start: LocalDate, end: LocalDate): Flow<List<ProgressEntry>>
}
