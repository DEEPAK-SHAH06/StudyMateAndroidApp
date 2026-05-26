package com.example.studymateandroidapp.data.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.Flashcard

data class ExamWithFlashcards(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "id",
        entityColumn = "examId"
    )
    val flashcards: List<Flashcard>
)
