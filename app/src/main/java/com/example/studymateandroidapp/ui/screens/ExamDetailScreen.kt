package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.ExamViewmodel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val examWithDetails by viewModel.getExamWithDetails(examId).collectAsState()

    examWithDetails?.let { details ->
        ExamDetailContent(
            details = details,
            onEdit = { onNavigateToEdit(examId) },
            onAddNote = { onNavigateToNotes(examId) },
            onAddFlashcard = { onNavigateToFlashcards(examId) },
            onStartStudy = { onStartStudy(examId) },
            onBack = onBack
        )
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ExamDetailContent(
    details: ExamWithDetails,
    onEdit: () -> Unit,
    onAddNote: () -> Unit,
    onAddFlashcard: () -> Unit,
    onStartStudy: () -> Unit,
    onBack: () -> Unit
) {
    val dateStr = LocalDate.ofEpochDay(details.exam.examDate / (24 * 60 * 60 * 1000))
        .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Exam Details",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartStudy,
                icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                text = { Text("Start Study Session") },
                containerColor = Color.Black,
                contentColor = Color.White
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                
                // Header Info
                Text(text = details.exam.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(text = details.exam.subject, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                
                Spacer(Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = dateStr, style = MaterialTheme.typography.bodyLarge)
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
                items(details.notes.take(3)) { note ->
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
                items(details.flashcards.take(3)) { card ->
                    FlashcardPreviewItem(card)
                }
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
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = Color.Black)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = note.content,
            modifier = Modifier.padding(16.dp),
            maxLines = 2,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FlashcardPreviewItem(card: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = "Q: ${card.question}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text = "A: ${card.answer}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
