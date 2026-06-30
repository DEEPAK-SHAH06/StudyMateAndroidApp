package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.ui.components.ConfirmDeleteDialog
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.NoteViewmodel

@Composable
fun NoteScreen(
    examId: Long? = null,
    viewModel: NoteViewmodel,
    onNavigateBack: () -> Unit,
    onNavigateToAddNote: (Long?) -> Unit,
    onNavigateToEditNote: (Long, Long?) -> Unit
) {
    val notes by viewModel.allNotes.collectAsState()
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    
    NoteContent(
        notes = notes.filter { examId == null || it.examId == examId },
        onBack = onNavigateBack,
        onAddNote = { onNavigateToAddNote(examId) },
        onEditNote = { noteId -> onNavigateToEditNote(noteId, examId) },
        onDeleteNote = { noteToDelete = it }
    )

    noteToDelete?.let { note ->
        ConfirmDeleteDialog(
            itemName = "Note",
            onConfirm = {
                viewModel.deleteNote(note)
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
        )
    }
}

@Composable
fun NoteContent(
    notes: List<Note>,
    onBack: () -> Unit,
    onAddNote: () -> Unit,
    onEditNote: (Long) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Study Notes",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", modifier = Modifier.size(35.dp))
            }
        }
    ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                            val imageRes = if (isDark) {
                                com.example.studymateandroidapp.R.drawable.studynotes_dark
                            } else {
                                com.example.studymateandroidapp.R.drawable.studynotes
                            }

                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(
                                text = "Add Your Notes",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Stay organized and revise smarter for better \nresults.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Notes for Exam :",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                if (notes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No notes found. Add one!",
                                color = Color.Gray
                            )
                        }
                    }
                }else {

                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onEdit = { onEditNote(note.id) },
                            onDelete = { onDeleteNote(note) }
                        )
                    }
                }
            }
        }
    }


@Composable
fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotePreview() {
    MaterialTheme {
        NoteContent(
            notes = listOf(Note(id = 1, title = "Mock Note", content = "Mock Note 1", examId = 1)),
            onBack = {},
            onAddNote = {},
            onEditNote = {},
            onDeleteNote = {}
        )
    }
}
