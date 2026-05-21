package com.example.studymateandroidapp.feature.flashcard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFlashcardScreen(
    examId: Long = -1L,
    cardId : Long? = null,
    onNavigateBack: () -> Unit = {}
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    val canSave = front.isNotBlank() && back.isNotBlank() && examId != -1L

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId == null) {
                    "New Flashcard"
                } else "Edit Flashcard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.back_arrow),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateBack()},
                        enabled = canSave,
                        modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.done),
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp),
                            contentDescription = "Done"
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)


        ){
            if (examId == -1L){
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        "⚠️ No exam linked. Please create flashcards from an Exam Detail screen.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Front Side",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it},

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    placeholder = { Text(text="Enter question or statement...") },
                    shape = MaterialTheme.shapes.large
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Back Side",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = back,
                    onValueChange = {back = it},

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp),
                    placeholder = { Text(text="Enter answer or definition...") },
                    shape = MaterialTheme.shapes.large
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditFlashcardPreview(){
    AddEditFlashcardScreen(examId = 1L)
}