package com.example.studymateandroidapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

import com.example.studymateandroidapp.data.model.CelebrationEvent
import com.example.studymateandroidapp.data.model.CelebrationType

/**
 * Celebration overlay that shows a fun confetti-style animation
 * and a contextual message whenever an achievement is unlocked, goal is completed, etc.
 */
@Composable
fun CelebrationOverlay(
    event: CelebrationEvent,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(2000) // Auto-dismiss after 2 seconds
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(200f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f))
                .padding(top = 48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Modern Emoji/Icon Container
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = event.icon.ifBlank { "✨" },
                            fontSize = 22.sp
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (event.type == CelebrationType.TASK_COMPLETED) {
                                "${event.subtitle} completed"
                            } else if (event.type == CelebrationType.GOAL_COMPLETED) {
                                "${event.subtitle} completed"
                            } else {
                                event.title
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (event.type != CelebrationType.TASK_COMPLETED && 
                            event.type != CelebrationType.GOAL_COMPLETED && 
                            event.subtitle.isNotBlank()) {
                            Text(
                                text = event.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (event.xpReward != null) {
                        Text(
                            text = "+${event.xpReward} XP",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
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
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
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
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("✕", color = MaterialTheme.colorScheme.onPrimary)
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
    Card(
        modifier = modifier.fillMaxWidth().height(170.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C121F)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "✨",
                    fontSize = 16.sp,
                    color = Color(0xFFFFD700)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Daily Inspiration",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFC084FC),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                lineHeight = 26.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "— $author",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFB0BEC5),
                fontWeight = FontWeight.SemiBold
            )
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
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "What did you study today?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "›",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
