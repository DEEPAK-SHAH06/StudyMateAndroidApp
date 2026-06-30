package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.ui.components.ConfirmDeleteDialog
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.FlashcardViewmodel

@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewmodel,
    examId: Long? = null,
    onStartStudy: (Long) -> Unit,
    onAddCard: () -> Unit,
    onAddCardWithId: (Long, Long) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit
) {
    val flashcards by viewModel.allFlashcards.collectAsState()
    var cardToDelete by remember { mutableStateOf<Flashcard?>(null) }
    
    FlashcardContent(
        flashcards = flashcards.filter { examId == null || it.examId == examId },
        examId = examId,
        onBack = onNavigateBack,
        onAddCard = onAddCard,
        onStartStudy = { onStartStudy(examId ?: -1L) },
        onEditCard = { card ->
            // Pass the cardId and examId to the navigation callback
            onAddCardWithId(card.id, card.examId)
        },
        onDeleteCard = { cardToDelete = it }
    )

    cardToDelete?.let { card ->
        ConfirmDeleteDialog(
            itemName = "Flashcard",
            onConfirm = {
                viewModel.deleteFlashcard(card)
                cardToDelete = null
            },
            onDismiss = { cardToDelete = null }
        )
    }
}

@Composable
fun FlashcardContent(
    flashcards: List<Flashcard>,
    examId: Long? = null,
    onBack: () -> Unit,
    onAddCard: () -> Unit,
    onStartStudy: () -> Unit,
    onEditCard: (Flashcard) -> Unit,
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard",modifier = Modifier.size(35.dp))
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
                modifier = Modifier.fillMaxWidth().size(170.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.9f))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                        val imageRes = if (isDark) {
                            R.drawable.flashcard_dark
                        } else {
                            R.drawable.flashcard
                        }

                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(70.dp)
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
                    FlashcardItem(
                        card = card,
                        onEdit = { onEditCard(card) },
                        onDelete = { onDeleteCard(card) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun FlashcardItem(card: Flashcard, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
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
            Row {
                IconButton(onClick = onEdit) {
                    Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
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
            Text(text = count, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,color = Color.Black )
            Text(text = label, style = MaterialTheme.typography.labelSmall, lineHeight = 14.sp, color = Color.Black)
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
            onEditCard = {},
            onDeleteCard = {}
        )
    }
}
