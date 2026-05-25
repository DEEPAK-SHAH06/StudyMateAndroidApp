package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    onNavigateBack: () -> Unit
) {
    val notes by viewModel.allNotes.collectAsState()
    
    NoteContent(
        notes = notes.filter { examId == null || it.examId == examId },
        onBack = onNavigateBack,
        onAddNote = { content ->
            viewModel.addNote(Note(content = content, examId = examId ?: 1L))
        },
        onDeleteNote = { viewModel.deleteNote(it) }
    )
}

@Composable
fun NoteContent(
    notes: List<Note>,
    onBack: () -> Unit,
    onAddNote: (String) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Study Notes",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
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
                    NoteItem(note = note, onDelete = { onDeleteNote(note) })
                }
            }
        }

        if (showAddDialog) {
            AddNoteDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { content ->
                    onAddNote(content)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun NoteItem(note: Note, onDelete: () -> Unit) {
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
            Text(text = note.content, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var content by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Note") },
        text = {
            TextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Note Content") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (content.isNotBlank()) onConfirm(content) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NotePreview() {
    MaterialTheme {
        NoteContent(
            notes = listOf(Note(id = 1, content = "Mock Note 1", examId = 1)),
            onBack = {},
            onAddNote = {},
            onDeleteNote = {}
        )
    }
}
