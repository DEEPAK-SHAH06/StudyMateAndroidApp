package com.example.studymateandroidapp.feature.flashcard.ui

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardStudyScreen(
    examId: Long = -1L,
    examTitle: String = "Preview Exam",
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(examId) {

    }

    val cards = remember {
        listOf(
            FlashcardData("What is Kotlin?", "A modern, statically typed programming language used for Android development."),
            FlashcardData("What is Jetpack Compose?", "Android's modern toolkit for building native UI using a declarative approach."),
            FlashcardData("What is a ViewModel?", "A class designed to store and manage UI-related data in a lifecycle-conscious way.")
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    Scaffold(

        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding().padding(top = 30.dp),
                title = { Text("Study Session :$examTitle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.back_arrow),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isFinished) {
                StudyFinishedView(
                    onRestart = {
                        currentIndex = 0
                        isFlipped = false
                        isFinished = false
                    },
                    onBack = onNavigateBack
                )
            } else {
                val currentCard = cards[currentIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Card ${currentIndex + 1} of ${cards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardView(
    card: FlashcardData,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable(onClick = onFlip),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke( 1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,

        ) {
            if (rotation <= 90f) {
                Text(
                    text = card.question,
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = card.answer,
                    modifier = Modifier
                        .padding(32.dp)
                        .graphicsLayer { rotationY = 180f },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
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
            border = BorderStroke(0.6.dp, Color.Gray),
            colors = ButtonDefaults.buttonColors(
               containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Icon(painter = painterResource(R.drawable.try_again), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }

        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Icon(painter = painterResource(R.drawable.again), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Got it!")
        }
    }
}

@Composable
fun StudyFinishedView(
    onRestart: () -> Unit,
    onBack: ()  -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.session_complete),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Study Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text("Your progress has been saved in Box levels.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(painter = painterResource(R.drawable.again), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Restart Session")
        }
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Exams")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardStudyPreview() {
        FlashcardStudyScreen()
}