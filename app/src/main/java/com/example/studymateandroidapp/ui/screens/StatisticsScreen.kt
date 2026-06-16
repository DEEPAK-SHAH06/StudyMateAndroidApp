package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.data.repository.StatisticsRepository
import com.example.studymateandroidapp.data.model.LevelInfo
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.ui.components.StreakCard
import com.example.studymateandroidapp.viewmodel.StatisticsViewmodel
import com.example.studymateandroidapp.viewmodel.StatisticsViewmodel.DailyChartPoint
import java.time.LocalDate

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewmodel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val levelInfo: LevelInfo? by viewModel.levelInfo.collectAsState(initial = null)

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    StatisticsContent(
        totalTasks         = uiState.totalTasks,
        completedTasks     = uiState.completedTasks,
        taskCompletionRate = uiState.taskCompletionRate,
        todayStudySeconds  = uiState.todayStudySeconds,
        completedGoals     = uiState.completedGoals,
        thisWeekStudySeconds = uiState.thisWeekStudySeconds,
        currentStreak      = uiState.currentStreak,
        bestStreak         = uiState.bestStreak,
        weeklyStreakStatus = uiState.weeklyStreakStatus,
        weeklyAverageSubtitle = uiState.weeklyAverageSubtitle,
        dailyChartData     = uiState.dailyChartData,
        levelInfo          = levelInfo,
        onBack             = onBack
    )
}

@Composable
fun StatisticsContent(
    totalTasks: Int,
    completedTasks: Int,
    taskCompletionRate: Int,
    todayStudySeconds: Int,
    completedGoals: Int,
    thisWeekStudySeconds: Int,
    currentStreak: Int,
    bestStreak: Int,
    weeklyStreakStatus: List<Boolean>,
    weeklyAverageSubtitle: String,
    dailyChartData: List<DailyChartPoint>,
    levelInfo: LevelInfo?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(title = "Statistics", onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Gamification Section ──────────────────────
            if (levelInfo != null) {
                LevelSection(levelInfo)
            }

            // ── Row 1 ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.CheckCircle,
                    label    = "Tasks Done",
                    value    = "$completedTasks/$totalTasks",
                    subtext  = "$taskCompletionRate%"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.Timer,
                    label    = "Study Today",
                    value    = StatisticsRepository.formatDuration(todayStudySeconds),
                    subtext  = "Keep it up!"
                )
            }

            // ── Row 2 ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.EmojiEvents,
                    label    = "Goals Met",
                    value    = "$completedGoals",
                    subtext  = "all time"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Default.TrendingUp,
                    label    = "This Week",
                    value    = StatisticsRepository.formatDuration(thisWeekStudySeconds),
                    subtext  = weeklyAverageSubtitle
                )
            }

            // ── Chart ─────────────────────────────────────
            Text(
                "Weekly Overview",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(top = 8.dp)
            )

            if (dailyChartData.isNotEmpty()) {
                WeeklyBarChart(
                    data     = dailyChartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                Surface(
                    color    = MaterialTheme.colorScheme.surfaceVariant,
                    shape    = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "Start studying to see your chart!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Streak Section ────────────────────────────
            Text(
                "Study Habit",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(top = 8.dp)
            )
            
            StreakCard(
                streakCount = currentStreak,
                bestStreak = bestStreak,
                weeklyStatus = weeklyStreakStatus,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun LevelSection(levelInfo: LevelInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🎓 Level ${levelInfo.level}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = levelInfo.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "XP Progress",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${levelInfo.currentLevelXp} / ${levelInfo.xpToNextLevel} XP",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { levelInfo.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Total XP: ${levelInfo.totalXp}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    subtext: String?
) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector     = icon,
                contentDescription = null,
                tint            = MaterialTheme.colorScheme.primary,
                modifier        = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text       = value,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (subtext != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = subtext,
                    style      = MaterialTheme.typography.labelSmall,
                    color      = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/** Gradient bar chart drawn with Canvas — no external chart library needed. */
@Composable
private fun WeeklyBarChart(
    data: List<DailyChartPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor          = MaterialTheme.colorScheme.primary
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val trackColor            = MaterialTheme.colorScheme.surfaceVariant
    val labelColor            = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val maxSeconds = (data.maxOfOrNull { it.seconds } ?: 1).coerceAtLeast(1)
                val barCount   = data.size
                val spacing    = 12.dp.toPx()
                val barWidth   = (size.width - (barCount - 1) * spacing) / barCount
                val chartH     = size.height

                data.forEachIndexed { index, point ->
                    val x         = index * (barWidth + spacing)
                    val barHeight = (point.seconds.toFloat() / maxSeconds) * chartH * 0.85f

                    // Background track
                    drawRoundRect(
                        color       = trackColor,
                        topLeft     = Offset(x, 0f),
                        size        = Size(barWidth, chartH),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )

                    // Filled bar with gradient
                    if (barHeight > 0) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(primaryColor, primaryContainerColor)
                            ),
                            topLeft      = Offset(x, chartH - barHeight),
                            size         = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Day labels below bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { point ->
                    Text(
                        text     = point.label,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = labelColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    MaterialTheme {
        StatisticsContent(
            totalTasks         = 8,
            completedTasks     = 4,
            taskCompletionRate = 50,
            todayStudySeconds  = 1800,
            completedGoals     = 2,
            thisWeekStudySeconds = 7200,
            currentStreak      = 5,
            bestStreak         = 12,
            weeklyStreakStatus = listOf(true, true, true, false, true, true, true),
            weeklyAverageSubtitle = "Avg: 17m 08s/day",
            dailyChartData     = emptyList(),
            levelInfo          = LevelInfo(
                totalXp = 1500,
                level = 5,
                currentLevelXp = 200,
                xpToNextLevel = 700,
                progress = 0.28f,
                title = "Scholar 🎓"
            ),
            onBack             = {}
        )
    }
}
