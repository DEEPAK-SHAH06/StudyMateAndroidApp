package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextOverflow
import com.example.studymateandroidapp.data.model.Achievement
import com.example.studymateandroidapp.data.model.AchievementProgress
import com.example.studymateandroidapp.data.model.AchievementType
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.MotivationViewModel

@Composable
fun AchievementsScreen(
    viewModel: MotivationViewModel,
    onNavigateBack: () -> Unit
) {
    val achievements by viewModel.allAchievements.collectAsStateWithLifecycle(emptyList())
    val progress by viewModel.achievementProgress.collectAsStateWithLifecycle(emptyList())

    AchievementsContent(
        achievements = achievements,
        progress = progress,
        onBack = onNavigateBack
    )
}

@Composable
fun AchievementsContent(
    achievements: List<Achievement>,
    progress: List<AchievementProgress>,
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
        containerColor = MaterialTheme.colorScheme.background
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
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
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
                    color = MaterialTheme.colorScheme.onBackground
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
                        val currentProgress = progress.find { it.type == type }
                        AchievementBadge(
                            type = type,
                            achievement = unlockedAchievement,
                            isUnlocked = type in unlockedTypes,
                            progress = currentProgress?.let { it.current to it.target },
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
    modifier: Modifier = Modifier,
    progress: Pair<Int, Int>? = null
) {
    val (emoji, title, desc) = achievementInfo(type)

    val bgColor =
        if (isUnlocked)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (isUnlocked)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.onSurfaceVariant

    val alpha = if (isUnlocked) 1f else 0.75f

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = bgColor.copy(alpha = alpha),
        border = if (isUnlocked)
            null
        else
            BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ICON AREA
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(emoji, fontSize = 28.sp)
                } else {
                    Icon(
                        Icons.Default.Lock,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = desc,
                color = textColor.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                minLines = 2
            )

            Spacer(Modifier.height(10.dp))

            // PROGRESS SECTION
            if (!isUnlocked && progress != null) {
                val (current, total) = progress
                val percent = current.toFloat() / total.coerceAtLeast(1)

                Text(
                    text = "$current / $total",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { percent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }

            // UNLOCKED STATE
            if (isUnlocked) {
                Spacer(Modifier.height(8.dp))

                if (isUnlocked && achievement != null) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = java.text.SimpleDateFormat(
                            "MMM d",
                            java.util.Locale.getDefault()
                        ).format(
                            java.util.Date(achievement.unlockedAt)
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
    AchievementType.POMODORO_MASTER -> Triple("🍅", "Pomodoro Master", "Complete 10 sessions")
    AchievementType.POMODORO_LEGEND -> Triple("👑", "Pomodoro Legend", "Complete 50 sessions")
    AchievementType.STREAK_3_DAY -> Triple("🔥", "First Spark", "3-day study streak!")
    AchievementType.STREAK_7_DAY -> Triple("📜", "Consistent Learner", "7-day study streak!")
    AchievementType.STREAK_30_DAY -> Triple("⚔️", "Study Warrior", "30-day study streak!")
    AchievementType.STREAK_100_DAY -> Triple("💎", "Unstoppable", "100-day study streak!")
    AchievementType.NIGHT_OWL -> Triple("🌙", "Night Owl", "Study late at night")
    AchievementType.EARLY_BIRD -> Triple("🌅", "Early Bird", "Study early morning")
    AchievementType.FLASHCARD_MASTER -> Triple("🧠", "Flashcard Master", "Create 50 flashcards")
    AchievementType.CONSISTENCY_KING -> Triple("👑", "Consistency King", "14-day streak")
    AchievementType.MARATHON_STUDIER -> Triple("📚", "Marathon Studier", "100 hours studied")
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
            progress = emptyList(),
            onBack = {}
        )
    }
}
