package com.example.studymateandroidapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.FlashcardViewmodel

@Composable
fun FlashcardStudyScreen(
    examId: Long,
    viewModel: FlashcardViewmodel,
    onNavigateBack: () -> Unit
) {
    val flashcards by viewModel.allFlashcards.collectAsState()
    val examCards = flashcards.filter { it.examId == examId }

    FlashcardStudyContent(
        cards = examCards,
        onBack = onNavigateBack
    )
}

@Composable
fun FlashcardStudyContent(
    cards: List<Flashcard>,
    onBack: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Study Session",
                onBack = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (isFinished || cards.isEmpty()) {
                StudyFinishedView(
                    onRestart = {
                        currentIndex = 0
                        isFlipped = false
                        isFinished = false
                    },
                    onBack = onBack,
                    isEmpty = cards.isEmpty()
                )
            } else {
                val currentCard = cards[currentIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Card ${currentIndex + 1} of ${cards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    FlashcardView(
                        card = currentCard,
                        isFlipped = isFlipped,
                        onFlip = { isFlipped = !isFlipped }
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    if (isFlipped) {
                        StudyControls(
                            onNext = {
                                if (currentIndex < cards.size - 1) {
                                    currentIndex++
                                    isFlipped = false
                                } else {
                                    isFinished = true
                                }
                            }
                        )
                    } else {
                        Text(
                            text = "Tap card to reveal answer",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardView(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardRotation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable(onClick = onFlip),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF8F8F8),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Text(
                    text = card.question,
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = card.answer,
                    modifier = Modifier
                        .padding(24.dp)
                        .graphicsLayer { rotationY = 180f },
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun StudyControls(onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black)
        ) {
            Text("Try Again")
        }

        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
        ) {
            Text("Got it!")
        }
    }
}

@Composable
fun StudyFinishedView(
    onRestart: () -> Unit,
    onBack: () -> Unit,
    isEmpty: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isEmpty) {
            Image(
                painter = painterResource(R.drawable.session_complete),
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Session Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Restart Session")
            }
        } else {
            Text(
                text = "No cards to study!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Exams")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardStudyPreview() {
    MaterialTheme {
        FlashcardStudyContent(
            cards = listOf(Flashcard(id = 1, question = "Question", answer = "Answer", examId = 1)),
            onBack = {}
        )
    }
}
