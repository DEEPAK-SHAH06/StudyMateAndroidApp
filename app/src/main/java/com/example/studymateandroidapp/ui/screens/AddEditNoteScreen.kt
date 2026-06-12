package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var content by remember { mutableStateOf("") }
    var existingNote by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            val note = viewModel.getNoteById(noteId)
            if (note != null) {
                existingNote = note
                content = note.content
            }
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
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (noteId == null) "NEW NOTE" else "EDIT NOTE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = if (noteId == null) "Capture Your Thoughts" else "Update Your Thoughts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Note Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        if (noteId == null) {
                            viewModel.addNote(
                                Note(
                                    content = content,
                                    examId = examId ?: 1L
                                )
                            )
                        } else {
                            existingNote?.let {
                                viewModel.updateNote(it.copy(content = content))
                            }
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Save Note", fontWeight = FontWeight.Bold)
            }
        }
    }
}
