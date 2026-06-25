package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entity representing a progress entry logged by the user.
 * Each entry records study time for a subject on a particular date.
 */
@Entity(tableName = "progress_entry")
data class ProgressEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val subject: String,
    val date: LocalDate,
    val durationMinutes: Int,
    val notes: String? = null
)
