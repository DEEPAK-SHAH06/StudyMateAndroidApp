package com.example.studymateandroidapp.feature.motivation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Celebration overlay that shows a fun confetti-style animation
 * and a message whenever an achievement is unlocked or a goal is completed.
 */
@Composable
fun CelebrationOverlay(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 0.8f),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            // Confetti particles
            ConfettiLayer()

            // Message card
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated trophy emoji
                    val scale by rememberInfiniteTransition(label = "trophy").animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "trophy_scale"
                    )
                    Text(
                        text = "🎉",
                        fontSize = (48 * scale).sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Awesome! 🚀", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfettiLayer() {
    val confettiColors = listOf(
        Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
        Color(0xFF95E1D3), Color(0xFFF38181), Color(0xFFA8E063),
        Color(0xFF6C5CE7), Color(0xFFFF8B94)
    )
    val emojis = listOf("⭐", "✨", "🌟", "💫", "🎊", "🎈", "🦋", "🌈")

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(12) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "confetti_$index")
            val yOffset by infiniteTransition.animateFloat(
                initialValue = -50f,
                targetValue = 1200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000 + (index * 150),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "y_$index"
            )
            val xPos = (index * 70 + 30).dp

            Text(
                text = emojis[index % emojis.size],
                fontSize = 20.sp,
                modifier = Modifier
                    .offset(x = xPos, y = yOffset.dp)
                    .zIndex(11f)
            )
        }
    }
}

/**
 * Inline encouragement banner shown within the Goals screen.
 */
@Composable
fun EncouragementBanner(
    message: String,
    onDismiss: () -> Unit
) {
    val brush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF6750A4), Color(0xFF9C27B0))
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("✕", color = Color.White)
            }
        }
    }
}

/**
 * Daily quote card widget for the Dashboard.
 */
@Composable
fun DailyQuoteCard(
    quote: String,
    author: String,
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
    )
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "✨ Daily Inspiration",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE0B0FF),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "\"$quote\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 26.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFB0BEC5),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Streak badge composable shown next to greeting.
 */
@Composable
fun StreakBadge(streak: Int) {
    if (streak < 1) return
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFFF6B35).copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", fontSize = 16.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${streak}d",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
        }
    }
}

/**
 * Reflection reminder card for the Dashboard — shown in the evening.
 */
@Composable
fun ReflectionReminderCard(
    onNavigateToReflection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onNavigateToReflection,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3E5F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "📝 Evening Reflection",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4)
                )
                Text(
                    "What did you study today?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6750A4).copy(alpha = 0.7f)
                )
            }
            Text(
                text = "›",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6750A4)
            )
        }
    }
}
