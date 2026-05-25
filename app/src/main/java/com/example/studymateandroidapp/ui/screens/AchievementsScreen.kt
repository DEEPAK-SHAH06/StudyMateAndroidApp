package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymateandroidapp.data.model.Achievement
import com.example.studymateandroidapp.data.model.AchievementType
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.MotivationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AchievementsScreen(
    viewModel: MotivationViewModel,
    onNavigateBack: () -> Unit
) {
    val achievements by viewModel.allAchievements.collectAsStateWithLifecycle(emptyList())

    AchievementsContent(
        achievements = achievements,
        onBack = onNavigateBack
    )
}

@Composable
fun AchievementsContent(
    achievements: List<Achievement>,
    onBack: () -> Unit
) {
    val unlockedTypes = achievements.map { it.type }.toSet()
    val allTypes = AchievementType.entries

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Achievements",
                onBack = onBack
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // Hero section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2D2D2D), Color(0xFF5A5A5A))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${achievements.size} Unlocked",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "of ${allTypes.size} total badges",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "All Badges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Grid layout using chunked rows
            val rows = allTypes.chunked(2)
            items(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { type ->
                        val unlockedAchievement = achievements.find { it.type == type }
                        AchievementBadge(
                            type = type,
                            achievement = unlockedAchievement,
                            isUnlocked = type in unlockedTypes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    type: AchievementType,
    achievement: Achievement?,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val (emoji, title, desc) = achievementInfo(type)
    val bgColor = if (isUnlocked) Color.White else Color(0xFFF0F0F0)
    val textColor = if (isUnlocked) Color.Black else Color.Gray

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        border = if (isUnlocked) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Color.Black else Color.Gray.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(emoji, fontSize = 28.sp)
                } else {
                    Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
            if (isUnlocked && achievement != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(achievement.unlockedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun achievementInfo(type: AchievementType): Triple<String, String, String> = when (type) {
    AchievementType.FIRST_TASK -> Triple("✅", "First Task!", "Complete your first task")
    AchievementType.TEN_TASKS -> Triple("📋", "Task Master", "Complete 10 tasks")
    AchievementType.FIFTY_TASKS -> Triple("🏅", "Task Legend", "Complete 50 tasks")
    AchievementType.FIRST_NOTE -> Triple("📝", "Note Taker", "Create your first note")
    AchievementType.TEN_NOTES -> Triple("📚", "Scribe", "Create 10 notes")
    AchievementType.FIRST_GOAL_COMPLETE -> Triple("🎯", "Goal Getter", "Complete your first goal")
    AchievementType.FIVE_GOALS_COMPLETE -> Triple("🌟", "Ambitious", "Complete 5 goals")
    AchievementType.STUDY_HOUR -> Triple("⏱️", "Hour Power", "Study for 1 hour")
    AchievementType.STUDY_TEN_HOURS -> Triple("🔥", "Marathon", "Study for 10 hours")
    AchievementType.SEVEN_DAY_STREAK -> Triple("🗓️", "Week Warrior", "7-day study streak")
    AchievementType.FOURTEEN_DAY_STREAK -> Triple("💪", "Fortnight Focus", "14-day streak")
    AchievementType.THIRTY_DAY_STREAK -> Triple("👑", "Monthly Master", "30-day streak")
    AchievementType.FIRST_FLASHCARD -> Triple("🃏", "Flash Scholar", "Create your first flashcard")
    AchievementType.FIRST_REFLECTION -> Triple("🪷", "Self Aware", "Write your first reflection")
}

@Preview(showBackground = true)
@Composable
fun AchievementsPreview() {
    MaterialTheme {
        AchievementsContent(
            achievements = listOf(
                Achievement(
                    type = AchievementType.FIRST_TASK,
                    unlockedAt = System.currentTimeMillis(),
                    title = "First Task!",
                    description = "Complete your first task"
                )
            ),
            onBack = {}
        )
    }
}
