package com.example.studymateandroidapp.data.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.Note

data class ExamWithNotes(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "id",
        entityColumn = "examId"
    )
    val notes: List<Note>
)
