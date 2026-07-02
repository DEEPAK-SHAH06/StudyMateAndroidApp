package com.example.studymateandroidapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.ExamViewmodel

@Composable
fun ExamDetailScreen(
    examId: Long,
    viewModel: ExamViewmodel,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToNotes: (Long) -> Unit,
    onNavigateToFlashcards: (Long) -> Unit,
    onStartStudy: (Long) -> Unit,
    onBack: () -> Unit
) {
    val examWithDetails by remember(examId) {
        viewModel.getExamWithDetails(examId)
    }.collectAsState(initial = null)

    val studySeconds by remember(examId) {
        viewModel.getStudySecondsForExam(examId)
    }.collectAsState(initial = 0)

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Exam Details",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onNavigateToEdit(examId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        floatingActionButton = {
            if (examWithDetails != null) {
                ExtendedFloatingActionButton(
                    onClick = { onStartStudy(examId) },
                    icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    text = { Text("Start Study Session") },
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = examWithDetails,
            transitionSpec = {
                fadeIn().togetherWith(fadeOut())
            },
            label = "ExamDetailContentTransition"
        ) { details ->
            if (details != null) {
                ExamDetailContent(
                    details = details,
                    studySeconds = studySeconds,
                    paddingValues = padding,
                    onAddNote = { onNavigateToNotes(examId) },
                    onAddFlashcard = { onNavigateToFlashcards(examId) }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ExamDetailContent(
    details: ExamWithDetails,
    studySeconds: Int,
    paddingValues: PaddingValues,
    onAddNote: () -> Unit,
    onAddFlashcard: () -> Unit
) {
    val dateTime = remember(details.exam.examDate) {
        java.time.LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(details.exam.examDate),
            java.time.ZoneId.systemDefault()
        )
    }
    val dateStr = remember(dateTime) {
        dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }
    val timeStr = remember(dateTime, details.exam.isTimeSet) {
        if (details.exam.isTimeSet) dateTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")) else null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
            .padding(horizontal = 26.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Spacer(Modifier.height(14.dp))

            // Header Info
            Text(text = details.exam.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = details.exam.subject, style = MaterialTheme.typography.titleMedium, color = Color.Gray)

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = dateStr, style = MaterialTheme.typography.bodyLarge)
                if (timeStr != null) {
                    Text(text = " • ", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Text(text = timeStr, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Exam Progress Section
        item {
            val progress = (studySeconds.toFloat() / (10f * 60f * 60f)).coerceAtMost(1f)
            val percentage = (progress * 100).toInt()
            val hours = studySeconds / 3600
            val minutes = (studySeconds % 3600) / 60

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Exam Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${hours}h ${minutes}m / 10h studied",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // Quick Stats
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailStatCard("Notes", details.notes.size.toString(), Icons.Default.Description, Modifier.weight(1f))
                DetailStatCard("Flashcards", details.flashcards.size.toString(), Icons.Default.Style, Modifier.weight(1f))
            }
        }

        // Notes Preview
        item {
            SectionHeaderWithAction("Notes", onAddNote)
        }
        if (details.notes.isEmpty()) {
            item { Text("No notes linked to this exam.", color = Color.Gray) }
        } else {
            items(
                details.notes.take(3),
                key = { note -> "note_${details.exam.id}_${note.id}" }
            ) { note ->
                NotePreviewItem(note)
            }
        }

        // Flashcards Preview
        item {
            SectionHeaderWithAction("Flashcards", onAddFlashcard)
        }
        if (details.flashcards.isEmpty()) {
            item { Text("No flashcards linked to this exam.", color = Color.Gray) }
        } else {
            items(
                details.flashcards.take(3),
                key = { card -> "flashcard_${details.exam.id}_${card.id}" }
            ) { card ->
                FlashcardPreviewItem(card)
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = onAction) {
            Text("View All")
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun DetailStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
    }
}

@Composable
fun NotePreviewItem(note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.content,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FlashcardPreviewItem(card: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = "Q: ${card.question}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text = "A: ${card.answer}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExamDetailPreview() {
    val mockExam = com.example.studymateandroidapp.data.model.Exam(
        id = 1,
        title = "Final Exam",
        subject = "Mathematics",
        examDate = System.currentTimeMillis() + 86400000,
        isTimeSet = true
    )
    val mockNotes = listOf(
        Note(id = 1, examId = 1, title = "Calculus", content = "Key formulas for calculus."),
        Note(id = 2, examId = 1, title = "Integration", content = "Review integration by parts.")
    )
    val mockFlashcards = listOf(
        Flashcard(id = 1, examId = 1, question = "What is the derivative of sin(x)?", answer = "cos(x)"),
        Flashcard(id = 2, examId = 1, question = "What is the integral of 1/x?", answer = "ln|x| + C")
    )
    val details = ExamWithDetails(
        exam = mockExam,
        notes = mockNotes,
        flashcards = mockFlashcards
    )

    MaterialTheme {
        ExamDetailContent(
            details = details,
            studySeconds = 7200,
            paddingValues = PaddingValues(0.dp),
            onAddNote = {},
            onAddFlashcard = {}
        )
    }
}