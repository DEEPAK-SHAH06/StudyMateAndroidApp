package com.example.studymateandroidapp.feature.flashcard.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R

class AddEditFlashcard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AddEditFlashcardScreen()

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFlashcardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Flashcard") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.back_arrow),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}, modifier = Modifier.size(48.dp)) {
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
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Front Side",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},

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
                    value = "",
                    onValueChange = {},

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
@Preview
@Composable
fun AddEditFlashcardPreview(){
    AddEditFlashcardScreen()
}

