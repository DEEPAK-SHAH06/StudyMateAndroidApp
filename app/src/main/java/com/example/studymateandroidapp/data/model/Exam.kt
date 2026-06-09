package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an upcoming or past exam.
 */
@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val examDate: Long, // Full date-time timestamp (Epoch millis)
    val isTimeSet: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
