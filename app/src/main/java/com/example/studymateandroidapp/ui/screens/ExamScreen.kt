package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.ExamViewmodel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.compose.ui.draw.clip
import com.example.studymateandroidapp.data.model.StudyProgress
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance

/**
 * Main screen for displaying and managing exams.
 *
 * @param viewModel The [ExamViewmodel] that provides data and handles actions.
 * @param onNavigateToAddExam Callback to navigate to the "Add Exam" screen.
 * @param onNavigateToEditExam Callback to navigate to the "Edit Exam" screen for a specific exam.
 * @param onStartStudy Callback to begin a study session for a specific exam.
 * @param onNavigateToNotes Callback to navigate to the notes for a specific exam.
 * @param onNavigateToFlashcards Callback to navigate to the flashcards for a specific exam.
 * @param onNavigateToAchievements Callback to navigate to the achievements screen.
 * @param onStatsClick Callback to navigate to the statistics screen.
 */
@Composable
fun ExamScreen(
    viewModel: ExamViewmodel,
    onNavigateToAddExam: () -> Unit,
    onNavigateToEditExam: (Long) -> Unit,
    onStartStudy: (Long) -> Unit,
    onNavigateToNotes: (Long) -> Unit,
    onNavigateToFlashcards: (Long) -> Unit,
    onNavigateToAchievements: () -> Unit,
    onStatsClick: () -> Unit
) {
    val exams by viewModel.allExams.collectAsState()
    val progress by viewModel.examProgress.collectAsState()

    ExamContent(
        exams = exams,
        progress = progress,
        onStatsClick   = onStatsClick,
        onNavigateToAchievements = onNavigateToAchievements,
        onAddExam = onNavigateToAddExam,
        onExamClick = onNavigateToEditExam,
        onDeleteExam = { viewModel.deleteExam(it) },
        onStartStudy = onStartStudy,
        onNotesClick = onNavigateToNotes,
        onFlashcardsClick = onNavigateToFlashcards
    )
}

/**
 * The content of the Exam screen, containing the search bar, upcoming exams, and past exams.
 *
 * @param exams The list of exams to display.
 * @param progress A map of exam IDs to their study progress.
 * @param onNavigateToAchievements Callback for the achievements icon.
 * @param onStatsClick Callback for the statistics icon.
 * @param onAddExam Callback for the add exam FAB.
 * @param onExamClick Callback for clicking an exam card.
 * @param onDeleteExam Callback for deleting an exam.
 * @param onStartStudy Callback for starting a study session.
 * @param onNotesClick Callback for viewing notes.
 * @param onFlashcardsClick Callback for viewing flashcards.
 */
@Composable
fun ExamContent(
    exams: List<Exam>,
    progress: Map<Long, StudyProgress>,
    onNavigateToAchievements: () -> Unit,
    onStatsClick: () -> Unit,
    onAddExam: () -> Unit,
    onExamClick: (Long) -> Unit,
    onDeleteExam: (Exam) -> Unit,
    onStartStudy: (Long) -> Unit,
    onNotesClick: (Long) -> Unit,
    onFlashcardsClick: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredExams = remember(exams, searchQuery) {
        val result = exams.filter { exam ->
            exam.title.contains(searchQuery, ignoreCase = true) ||
                    exam.subject.contains(searchQuery, ignoreCase = true)
        }
        result
    }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "",

                actions = {
                    IconButton(onClick = onNavigateToAchievements) {
                        Icon(
                            painter = painterResource(id = R.drawable.achievements),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Achievements",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onStatsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.statistics),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Statistics",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExam,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exam", modifier = Modifier.size(35.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp)
        ) {
            Text(
                "Exams :",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp
            )

            Spacer(Modifier.height(12.dp))
            ExamSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val imageRes = if (isDark) {
                    R.drawable.exam_dark
                } else {
                    R.drawable.exam
                }

                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }

            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.End),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            val currentTime = System.currentTimeMillis()
            val upcomingExams = filteredExams.filter { it.examDate >= currentTime }.sortedBy { it.examDate }
            val pastExams = filteredExams.filter { it.examDate < currentTime }.sortedByDescending { it.examDate }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (upcomingExams.isEmpty()) {
                    item {
                        Text(
                            text = if (searchQuery.isEmpty()) "No upcoming exams. Stay prepared!" else "No exams match your search.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(upcomingExams, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
                            progress = progress[exam.id],
                            onClick = { onExamClick(exam.id) },
                            onDelete = { onDeleteExam(exam) },
                            onStudy = { onStartStudy(exam.id) },
                            onNotes = { onNotesClick(exam.id) },
                            onFlashcards = { onFlashcardsClick(exam.id) }
                        )
                    }
                }

                if (pastExams.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Past Exams",
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.End),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(pastExams, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
                            progress = progress[exam.id],
                            isPast = true,
                            onClick = { onExamClick(exam.id) },
                            onDelete = { onDeleteExam(exam) },
                            onStudy = { onStartStudy(exam.id) },
                            onNotes = { onNotesClick(exam.id) },
                            onFlashcards = { onFlashcardsClick(exam.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * A search bar for filtering exams by title or subject.
 *
 * @param query The current search query.
 * @param onQueryChange Callback when the search query changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth()
            .height(49.dp),
        placeholder = { Text("Search exams, subjects...",
            fontSize = 13.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/**
 * A card representing an exam, showing its details, progress, and actions.
 *
 * @param exam The exam to display.
 * @param progress The study progress for this exam.
 * @param isPast Whether this is a past exam.
 * @param onClick Callback when the card is clicked.
 * @param onDelete Callback when the delete icon is clicked.
 * @param onStudy Callback when the "Study" button is clicked.
 * @param onNotes Callback when the notes icon is clicked.
 * @param onFlashcards Callback when the flashcards icon is clicked.
 */
@Composable
fun ExamCard(
    exam: Exam,
    progress: StudyProgress? = null,
    isPast: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onStudy: () -> Unit,
    onNotes: () -> Unit,
    onFlashcards: () -> Unit
) {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(exam.examDate), ZoneId.systemDefault())
    val dateStr = dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    val timeStr = if (exam.isTimeSet) dateTime.format(DateTimeFormatter.ofPattern("hh:mm a")) else ""

    val completion = progress?.completionPercentage ?: 0f
    val isMastered = completion >= 1.0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = when {
            isMastered -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isMastered) 2.dp else 1.dp,
            color = when {
                isMastered -> Color(0xFF4CAF50)
                isPast -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = exam.title,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isPast)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        if (isMastered) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF4CAF50),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "MASTERED",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(text = exam.subject, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (timeStr.isNotEmpty()) {
                            Text(text = " • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = timeStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (completion > 0f) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { completion },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color =
                        if (isMastered)
                            Color(0xFF4CAF50)
                        else
                            MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(completion * 100).toInt()}% Mastered",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMastered)
                        Color(0xFF4CAF50)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isMastered) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (!isPast) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNotes, modifier = Modifier.size(24.dp)) {

                            Icon(painter = painterResource(id = R.drawable.notes), contentDescription = "Notes", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = onFlashcards, modifier = Modifier.size(24.dp)) {
                            Icon(painter = painterResource(id = R.drawable.baseline_file_copy_24), contentDescription = "Flashcards", tint = Color.Black, modifier = Modifier.size(20.dp))

                        }
                    }

                    Button(
                        onClick = onStudy,
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (isMastered)
                                    Color(0xFF4CAF50)
                                else
                                    MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.start),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Study", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ExamScreenPreview() {
    val mockExams = listOf(
        Exam(id = 1, title = "Final Exam", subject = "Mathematics", examDate = System.currentTimeMillis() + 86400000 * 10),
        Exam(id = 2, title = "Midterm", subject = "Physics", examDate = System.currentTimeMillis() + 86400000 * 5)
    )
    MaterialTheme {
        ExamContent(
            exams = mockExams,
            progress = emptyMap(),
            onAddExam = {},
            onExamClick = {},
            onDeleteExam = {},
            onStartStudy = {},
            onNavigateToAchievements = {},
            onStatsClick = {},
            onNotesClick = {},
            onFlashcardsClick = {}
        )
    }
}