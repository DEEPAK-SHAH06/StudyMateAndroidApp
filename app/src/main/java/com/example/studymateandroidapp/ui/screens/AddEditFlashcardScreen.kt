package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.FlashcardViewmodel

@Composable
fun AddEditFlashcardScreen(
    examId: Long = -1L,
    cardId: Long? = null,
    viewModel: FlashcardViewmodel,
    onNavigateBack: () -> Unit
) {
    AddEditFlashcardContent(
        isEdit = cardId != null,
        examId = examId,
        onBack = onNavigateBack,
        onSave = { front, back ->
            if (cardId == null) {
                viewModel.addFlashcard(Flashcard(question = front, answer = back, examId = examId))
            } else {
                viewModel.updateFlashcard(Flashcard(id = cardId, question = front, answer = back, examId = examId))
            }
            onNavigateBack()
        }
    )
}

@Composable
fun AddEditFlashcardContent(
    isEdit: Boolean,
    examId: Long,
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    val canSave = front.isNotBlank() && back.isNotBlank() && examId != -1L

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = if (isEdit) "Edit Flashcard" else "New Flashcard",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onSave(front, back) }, enabled = canSave) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (examId == -1L) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "⚠️ No exam linked. Please create flashcards from an Exam Detail screen.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Front Side",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    placeholder = { Text("Enter question or statement...") },
                    shape = MaterialTheme.shapes.large
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Back Side",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = back,
                    onValueChange = { back = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                    placeholder = { Text("Enter answer or definition...") },
                    shape = MaterialTheme.shapes.large
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (canSave) onSave(front, back) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = canSave,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Flashcard", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditFlashcardPreview() {
    MaterialTheme {
        AddEditFlashcardContent(isEdit = false, examId = 1L, onBack = {}, onSave = { _, _ -> })
    }
}
