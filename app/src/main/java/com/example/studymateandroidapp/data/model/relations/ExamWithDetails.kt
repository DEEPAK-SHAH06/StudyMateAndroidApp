package com.example.studymateandroidapp.data.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.Note

data class ExamWithDetails(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "id",
        entityColumn = "examId"
    )
    val notes: List<Note>,
    @Relation(
        parentColumn = "id",
        entityColumn = "examId"
    )
    val flashcards: List<Flashcard>
)
