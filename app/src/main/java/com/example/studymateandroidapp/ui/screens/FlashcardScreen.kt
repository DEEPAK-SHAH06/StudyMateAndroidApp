package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.FlashcardViewmodel

@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewmodel,
    examId: Long? = null,
    onStartStudy: (Long) -> Unit,
    onAddCard: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val flashcards by viewModel.allFlashcards.collectAsState()
    
    FlashcardContent(
        flashcards = flashcards.filter { examId == null || it.examId == examId },
        examId = examId,
        onBack = onNavigateBack,
        onAddCard = onAddCard,
        onStartStudy = { onStartStudy(examId ?: -1L) },
        onDeleteCard = { viewModel.deleteFlashcard(it) }
    )
}

@Composable
fun FlashcardContent(
    flashcards: List<Flashcard>,
    examId: Long? = null,
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onStartStudy: () -> Unit,
    onDeleteCard: (Flashcard) -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Flashcard Decks",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCard,
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Study Card
            Surface(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF7F7F7),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Image(
                            painter = painterResource(id = R.drawable.flashcard),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Study All Cards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "${flashcards.size} cards ready", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }

                    IconButton(
                        onClick = onStartStudy,
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Black)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.start), contentDescription = "Start", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatItem(count = "${flashcards.size}", label = "Total\ncards", Color(0xFFE3F2FD), Modifier.weight(1f))
                StatItem(count = "${flashcards.size}", label = "To\nreview", Color(0xFFFCE4EC), Modifier.weight(1f))
                StatItem(count = if (examId != null) "1" else "All", label = "Exam\nsubjects", Color(0xFFE8F5E9), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Cards in this Deck", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(flashcards) { card ->
                    FlashcardItem(card = card, onDelete = { onDeleteCard(card) })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun FlashcardItem(card: Flashcard, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = card.question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = card.answer, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String, backgroundColor: Color, modifier: Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(text = count, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, lineHeight = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardPreview() {
    MaterialTheme {
        FlashcardContent(
            flashcards = listOf(Flashcard(id = 1, question = "Front", answer = "Back", examId = 1)),
            onBack = {},
            onAddCard = {},
            onStartStudy = {},
            onDeleteCard = {}
        )
    }
}
