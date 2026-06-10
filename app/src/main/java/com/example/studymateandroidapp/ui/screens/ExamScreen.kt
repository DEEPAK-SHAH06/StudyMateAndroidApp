package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ExamScreen(
    viewModel: ExamViewmodel,
    onNavigateToAddExam: () -> Unit,
    onNavigateToEditExam: (Long) -> Unit,
    onStartStudy: (Long) -> Unit,
    onNavigateToNotes: (Long) -> Unit,
    onNavigateToFlashcards: (Long) -> Unit
) {
    val exams by viewModel.allExams.collectAsState()

    ExamContent(
        exams = exams,
        onAddExam = onNavigateToAddExam,
        onExamClick = onNavigateToEditExam,
        onDeleteExam = { viewModel.deleteExam(it) },
        onStartStudy = onStartStudy,
        onNotesClick = onNavigateToNotes,
        onFlashcardsClick = onNavigateToFlashcards
    )
}

@Composable
fun ExamContent(
    exams: List<Exam>,
    onAddExam: () -> Unit,
    onExamClick: (Long) -> Unit,
    onDeleteExam: (Exam) -> Unit,
    onStartStudy: (Long) -> Unit,
    onNotesClick: (Long) -> Unit,
    onFlashcardsClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Exams :",
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExam,
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exam")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.exam),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }

            Text(
                text = "Upcoming Exams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val currentTime = System.currentTimeMillis()
            val upcomingExams = exams.filter { it.examDate >= currentTime }.sortedBy { it.examDate }
            val pastExams = exams.filter { it.examDate < currentTime }.sortedByDescending { it.examDate }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (upcomingExams.isEmpty()) {
                    item {
                        Text(
                            text = "No upcoming exams. Stay prepared!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(upcomingExams, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(pastExams, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
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
            
            Spacer(modifier = Modifier.height(80.dp)) // FAB space
        }
    }
}

@Composable
fun ExamCard(
    exam: Exam,
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isPast) Color(0xFFF9F9F9) else Color(0xFFF2F2F2),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isPast) Color.LightGray.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = if (isPast) R.drawable.exam else R.drawable.upcoming_exam),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = if (isPast) Color.Gray else Color.Unspecified
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam.title,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isPast) Color.Gray else Color.Black
                    )
                    Text(text = exam.subject, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        if (timeStr.isNotEmpty()) {
                            Text(text = " • ", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(text = timeStr, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = if (isPast) Color.LightGray else Color.DarkGray
                    )
                }
            }

            if (!isPast) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNotes, modifier = Modifier.size(24.dp)) {
                            Icon(painter = painterResource(id = R.drawable.notes), contentDescription = "Notes")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = onFlashcards, modifier = Modifier.size(24.dp)) {
                            Icon(painter = painterResource(id = R.drawable.baseline_file_copy_24), contentDescription = "Flashcards")
                        }
                    }

                    Button(
                        onClick = onStudy,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.start),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Study", style = MaterialTheme.typography.labelLarge, color = Color.White)
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
            onAddExam = {},
            onExamClick = {},
            onDeleteExam = {},
            onStartStudy = {},
            onNotesClick = {},
            onFlashcardsClick = {}
        )
    }
}
