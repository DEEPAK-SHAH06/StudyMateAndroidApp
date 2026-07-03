package com.example.studymateandroidapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.FlashcardViewmodel
import kotlin.collections.filter

@Composable
fun FlashcardStudyScreen(
    examId: Long,
    viewModel: FlashcardViewmodel,
    onNavigateBack: () -> Unit
) {
    val flashcards by viewModel.allFlashcards.collectAsState()
    val examCards = remember(flashcards, examId) {
        flashcards
            .filter { it.examId == examId }
            .shuffled()
    }
    FlashcardStudyContent(
        cards = examCards,
        onBack = onNavigateBack,
        onSessionComplete = { correct: Int, total: Int ->
            viewModel.completeFlashcardSession(examId, correct, total)
        }
    )
}

@Composable
fun FlashcardStudyContent(
    cards: List<Flashcard>,
    onBack: () -> Unit,
    onSessionComplete: (Int, Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var shuffledCards by remember(cards) { mutableStateOf(cards.shuffled()) }
    var reviewCount by remember { mutableIntStateOf(0) }

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
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isFinished || cards.isEmpty()) {
                StudyFinishedView(
                    correct = correctCount,
                    total = cards.size,
                    onRestart = {
                        shuffledCards = cards.shuffled()
                        currentIndex = 0
                        correctCount = 0
                        isFlipped = false
                        isFinished = false
                    },
                    onBack = onBack,
                    isEmpty = cards.isEmpty()
                )
            } else {
                val currentCard = shuffledCards[currentIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Card ${currentIndex + 1} of ${shuffledCards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    FlashcardView(
                        card = currentCard,
                        isFlipped = isFlipped,
                        onFlip = { isFlipped = !isFlipped }
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    if (isFlipped) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Did you remember it?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            StudyControls(
                                onCorrect = {
                                    val newCorrect = correctCount + 1
                                    correctCount = newCorrect

                                    if (currentIndex < shuffledCards.size - 1) {
                                        currentIndex++
                                        isFlipped = false
                                    } else {
                                        isFinished = true
                                        onSessionComplete(newCorrect, shuffledCards.size)
                                    }
                                },
                                onIncorrect = {
                                    reviewCount++

                                    if (currentIndex < shuffledCards.size - 1) {
                                        currentIndex++
                                        isFlipped = false
                                    } else {
                                        isFinished = true
                                        onSessionComplete(correctCount, shuffledCards.size)
                                    }
                                }
                            )
                        }
                    } else {
                        Text(
                            text = "Tap the card to reveal the answer",
                            style = MaterialTheme.typography.bodyMedium,
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
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
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
fun StudyControls(onCorrect: () -> Unit, onIncorrect: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onIncorrect,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black)
        ) {
            Text("Try Again")
        }

        Button(
            onClick = onCorrect,
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
    correct: Int,
    total: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    isEmpty: Boolean = false
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isEmpty) {
            val imageRes = if (isDark) {
                R.drawable.session_dark
            } else {
                R.drawable.sessioncomplete
            }

            Image(
                painter = painterResource(imageRes),
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

            val percentage = if (total == 0) 0 else (correct * 100 / total)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$correct / $total Correct",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when {
                    percentage == 100 -> "Perfect! 🎉"
                    percentage >= 80 -> "Great job! Keep it up 💪"
                    percentage >= 60 -> "Nice work! Review a few more cards 📚"
                    else -> "Keep practicing—you've got this! 🌱"
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Restart Session")
            }
        } else {
            Image(
                painter = painterResource(
                    if (isDark)
                        R.drawable.flashcard_dark
                    else
                        R.drawable.flashcard
                ),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your deck is empty",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create a few flashcards before starting your study session.",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Flashcard deck section. ")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardStudyPreview() {
    MaterialTheme {
        FlashcardStudyContent(
            cards = listOf(Flashcard(id = 1, question = "Question", answer = "Answer", examId = 1)),
            onBack = {},
            onSessionComplete = { _, _ -> }
        )
    }
}
