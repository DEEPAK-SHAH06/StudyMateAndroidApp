package com.studyplanner.core.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.studyplanner.core.model.Exam
import com.studyplanner.core.model.Note

data class ExamWithNotes(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "id",
        entityColumn = "examId"
    )
    val notes: List<Note>
)
