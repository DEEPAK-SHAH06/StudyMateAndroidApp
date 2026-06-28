package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.NoteViewmodel

@Composable
fun AddEditNoteScreen(
    noteId: Long? = null,
    examId: Long? = null,
    viewModel: NoteViewmodel,
    onNavigateBack: () -> Unit
) {
    val exams by viewModel.allExams.collectAsState()
    var content by remember { mutableStateOf("") }
    var existingNote by remember { mutableStateOf<Note?>(null) }
    var selectedExamId by remember(examId) { mutableStateOf(examId) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            val note = viewModel.getNoteById(noteId)
            if (note != null) {
                existingNote = note
                content = note.content
                selectedExamId = note.examId
            }
        }
    }

    // Automatically select the first exam if none provided and exams are available
    LaunchedEffect(exams) {
        if (selectedExamId == null && exams.isNotEmpty()) {
            selectedExamId = exams.first().id
        }
    }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = if (noteId == null) "Add Note" else "Edit Note",
                onBack = onNavigateBack
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 28.dp,
                    end = 28.dp,
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f))
                {
                    Text(
                        text = if (noteId == null) "NEW NOTE" else "EDIT NOTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (noteId == null)
                            "Capture Your Thoughts"
                        else
                            "Update Your Thoughts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val imageRes = if (isDark) {
                    R.drawable.addnote_dark
                } else {
                    R.drawable.addnote
                }
                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (selectedExamId == null) {
                Text(
                    text = "Please create an exam first before adding notes.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Note Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val finalExamId = selectedExamId
                    if (content.isNotBlank() && finalExamId != null) {
                        if (noteId == null) {
                            viewModel.addNote(
                                Note(
                                    content = content,
                                    examId = finalExamId
                                )
                            )
                        } else {
                            existingNote?.let {
                                viewModel.updateNote(
                                    it.copy(
                                        content = content,
                                        examId = finalExamId
                                            )
                                        )
                                    }
                                }
                                onNavigateBack()
                            }
                        },
                        enabled = content.isNotBlank() && selectedExamId != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "Save Note",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
        }


    }
}
