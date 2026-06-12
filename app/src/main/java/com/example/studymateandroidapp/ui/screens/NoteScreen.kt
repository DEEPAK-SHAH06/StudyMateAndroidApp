package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.data.model.Note
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
    
    NoteContent(
        notes = notes.filter { examId == null || it.examId == examId },
        onBack = onNavigateBack,
        onAddNote = { onNavigateToAddNote(examId) },
        onEditNote = { noteId -> onNavigateToEditNote(noteId, examId) },
        onDeleteNote = { viewModel.deleteNote(it) }
    )
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
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No notes found. Add one!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
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
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
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
            notes = listOf(Note(id = 1, content = "Mock Note 1", examId = 1)),
            onBack = {},
            onAddNote = {},
            onEditNote = {},
            onDeleteNote = {}
        )
    }
}
