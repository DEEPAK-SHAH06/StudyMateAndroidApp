package com.example.studymateandroidapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * All navigation destinations in the app.
 *
 * Sealed class provides exhaustive `when` matching and type safety.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // ── Bottom nav destinations ───────────────────────────
    data object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    data object Tasks : Screen("tasks", "Tasks", Icons.Default.CheckCircle)
    data object StudyTimer : Screen("study_timer?examId={examId}", "Timer", Icons.Default.Timer) {
        fun createRoute(examId: Long? = null): String {
            return if (examId != null) {
                "study_timer?examId=$examId"
            } else {
                "study_timer"
            }
        }
    }
    data object Exams : Screen("exams", "Exams", Icons.Default.MenuBook)
    data object Statistics : Screen("statistics", "Stats", Icons.Default.BarChart)
    data object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    data object Notes : Screen("notes", "Notes", Icons.Default.Description) {
        fun createRoute(examId: Long? = null) = if (examId != null) "notes?examId=$examId" else "notes"
    }

    // ── Detail / form destinations ────────────────────────
    data object AddTask : Screen("add_task", "Add Task")
    data object EditTask : Screen("edit_task/{taskId}", "Edit Task") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    data object AddExam : Screen("add_exam", "Add Exam")
    data object EditExam : Screen("edit_exam/{examId}", "Edit Exam") {
        fun createRoute(examId: Long) = "edit_exam/$examId"
    }
    data object ExamDetail : Screen("exam_detail/{examId}", "Exam Details") {
        fun createRoute(examId: Long) = "exam_detail/$examId"
    }
    data object Goals : Screen("goals", "Goals")
    data object AddGoal : Screen("add_goal", "Add Goal")
    data object AddNote : Screen("add_note?noteId={noteId}&examId={examId}", "Add Note") {
        fun createRoute(noteId: Long? = null, examId: Long? = null): String {
            return when {
                noteId != null && examId != null -> "add_note?noteId=$noteId&examId=$examId"
                noteId != null -> "add_note?noteId=$noteId"
                examId != null -> "add_note?examId=$examId"
                else -> "add_note"
            }
        }
    }
    data object Flashcards : Screen("flashcards/{examId}", "Flashcards") {
        fun createRoute(examId: Long) = "flashcards/$examId"
    }
    data object StudyFlashcards : Screen("study_flashcards/{examId}", "Study") {
        fun createRoute(examId: Long) = "study_flashcards/$examId"
    }
    data object AddFlashcard : Screen("add_flashcard?cardId={cardId}&examId={examId}", "Add Flashcard") {
        fun createRoute(cardId: Long? = null, examId: Long? = null): String {
            return when {
                cardId != null && examId != null -> "add_flashcard?cardId=$cardId&examId=$examId"
                cardId != null -> "add_flashcard?cardId=$cardId"
                examId != null -> "add_flashcard?examId=$examId"
                else -> "add_flashcard"
            }
        }
    }
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object EditProfile : Screen("edit_profile", "Edit Profile")
    data object DailyReflection : Screen("daily_reflection", "Reflection", Icons.Default.AutoAwesome)
    data object Achievements : Screen("achievements", "Achievements", Icons.Default.EmojiEvents)

    companion object {
        /** Destinations shown in the bottom navigation bar. */
        val bottomNavItems = listOf(Dashboard, Tasks, Exams, StudyTimer, Settings)
    }
}
