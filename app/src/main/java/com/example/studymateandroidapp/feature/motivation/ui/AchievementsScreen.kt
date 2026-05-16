package com.example.studymateandroidapp.feature.motivation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.studymateandroidapp.core.model.Achievement
import com.example.studymateandroidapp.core.model.AchievementType
import com.example.studymateandroidapp.feature.motivation.data.MotivationRepository
import com.example.studymateandroidapp.feature.motivation.viewmodel.MotivationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: MotivationViewModel,
    onNavigateBack: () -> Unit
) {
//    val achievements by viewModel.allAchievements.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Achievements", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                listOf(Color(0xFF6750A4), Color(0xFF9C27B0))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "",
//                            "${achievements.size} Unlocked",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "of ${AchievementType.entries.size} total badges",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // All achievement types
//            val unlockedTypes = achievements.map { it.type }.toSet()
            val allTypes = AchievementType.entries

            item {
                Text(
                    "All Badges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Grid of 2
            val rows = allTypes.chunked(2)
            items(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { type ->
//                        val unlockedAchievement = achievements.find { it.type == type }
                        AchievementBadge(
                            type = type,
//                            achievement = unlockedAchievement,
//                            isUnlocked = type in unlockedTypes,
                            achievement = null,
                            isUnlocked = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Preview
@Composable
private fun AchievementsScreenPreview() {
    //AchievementsScreen(viewModel = MotivationViewModel(MotivationRepository()), onNavigateBack = {})
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

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if (isUnlocked) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked)
                            Brush.linearGradient(listOf(Color(0xFF6750A4), Color(0xFF9C27B0)))
                        else
                            Brush.linearGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(emoji, fontSize = 24.sp)
                } else {
                    Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (isUnlocked && achievement != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = SimpleDateFormat("MMM d", Locale.getDefault())
                        .format(Date(achievement.unlockedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6750A4),
                    fontWeight = FontWeight.SemiBold
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
