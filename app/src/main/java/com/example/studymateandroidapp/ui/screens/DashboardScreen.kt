package com.example.studymateandroidapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.viewmodel.DashboardViewModel
import com.example.studymateandroidapp.ui.components.CelebrationOverlay
import com.example.studymateandroidapp.viewmodel.DashboardViewModel.GoalSummary
import com.example.studymateandroidapp.ui.components.DailyQuoteCard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReflection: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Celebration overlay
    if (uiState.showCelebration) {
        CelebrationOverlay(
            message = uiState.celebrationMessage,
            isVisible = uiState.showCelebration,
            onDismiss = viewModel::dismissCelebration
        )
    }

    DashboardContent(
        greeting = uiState.greeting,
        userName = uiState.userName,
        userBio = uiState.userBio,
        userPhotoUrl = uiState.userPhotoUrl,
        currentStreak = uiState.currentStreak,
        todayTasks = uiState.todayTasks,
        pendingTaskCount = uiState.pendingTaskCount,
        todayStudyFormatted = uiState.todayStudyFormatted,
        todayStudyMinutes = uiState.todayStudyMinutes,
        nextExamTitle = uiState.nextExam?.title,
        daysUntilNextExam = uiState.daysUntilNextExam,
        activeGoals = uiState.activeGoals,
        isLoading = uiState.isLoading,
        showSyncPrompt = uiState.showSyncPrompt,
        dailyQuote = uiState.dailyQuote,
        dailyQuoteAuthor = uiState.dailyQuoteAuthor,
        showReflectionPrompt = uiState.showReflectionPrompt,
        onTaskToggle = { id, completed -> viewModel.onTaskCompletionToggled(id, completed) },
        onNavigateToTasks = onNavigateToTasks,
        onNavigateToTimer = onNavigateToTimer,
        onNavigateToExams = onNavigateToExams,
        onNavigateToGoals = onNavigateToGoals,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToStats = onNavigateToStats,
        onNavigateToCalendar = onNavigateToCalendar,
        onNavigateToReflection = onNavigateToReflection,
        onNavigateToAchievements = onNavigateToAchievements
    )
}

@Composable
private fun DashboardContent(
    greeting: String,
    userName: String,
    userBio: String,
    userPhotoUrl: String?,
    currentStreak: Int,
    todayTasks: List<Task>,
    pendingTaskCount: Int,
    todayStudyFormatted: String,
    todayStudyMinutes: Int,
    nextExamTitle: String?,
    daysUntilNextExam: Long?,
    activeGoals: List<GoalSummary>,
    isLoading: Boolean,
    showSyncPrompt: Boolean,
    dailyQuote: String,
    dailyQuoteAuthor: String,
    showReflectionPrompt: Boolean,
    onTaskToggle: (Long, Boolean) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReflection: () -> Unit,
    onNavigateToAchievements: () -> Unit
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StudyMateTopBar(
                title = "",
                onBack = null,
                actions = {
                    IconButton(onClick = onNavigateToAchievements) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCalendar,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 28.dp, end = 28.dp, bottom = 28.dp)
        ) {
            // ── 1. Profile Header ────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        if (userPhotoUrl != null) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = greeting.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Hi, $userName",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (currentStreak > 0) {
                            Text(
                                text = "🔥 $currentStreak Day Streak",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = userBio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── 2. Daily Inspiration Quote ──────────────────
            if (dailyQuote.isNotBlank()) {
                item {
                    DailyQuoteCard(quote = dailyQuote, author = dailyQuoteAuthor)
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── 3. Sync Card ──────────────────
            if (showSyncPrompt) {
                item {
                    Surface(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Keep your data safe", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Sign in with Google to enable auto-sync and backup.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                            Icon(Icons.Default.CloudSync, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ── 4. Featured Exam Card ──────────────────────
            if (nextExamTitle != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Surface(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(50.dp)) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = when {
                                            daysUntilNextExam == 0L -> "HAPPENING TODAY"
                                            daysUntilNextExam == 1L -> "DUE TOMORROW"
                                            else -> "STARTS IN $daysUntilNextExam DAYS"
                                        },
                                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(text = nextExamTitle, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            Text("Preparing for your next big challenge.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToTimer,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Start Session", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ── 5. Focus Pulse ───────────────────────────
            item {
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Focus Pulse", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Total focus: $todayStudyFormatted today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Spacer(Modifier.height(16.dp))
                            FocusStatRow(Icons.Default.FlashOn, "Deep Work", "${(todayStudyMinutes * 0.6).toInt()}m")
                            FocusStatRow(Icons.Default.MenuBook, "Study", "${(todayStudyMinutes * 0.4).toInt()}m")
                            
                            Spacer(Modifier.height(12.dp))
                            Text("Full insights →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onNavigateToStats() })
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── 6. Today's Plan ────────────────────
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Today's Plan", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                    TextButton(onClick = onNavigateToTasks) { Text("View All", color = MaterialTheme.colorScheme.primary) }
                }
            }

            if (todayTasks.isEmpty()) {
                item {
                    Text("No tasks for today!", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                items(todayTasks.take(3)) { task ->
                    AestheticTaskRow(task = task, onToggle = { onTaskToggle(task.id, !task.isCompleted) })
                }
            }

            // ── 7. Evening Reflection ────────────────────
            if (showReflectionPrompt) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        onClick = onNavigateToReflection,
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RateReview, null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(16.dp))
                            Text("Evening Reflection", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusStatRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AestheticTaskRow(task: Task, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (task.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            Text(task.priority.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    MaterialTheme {
        DashboardContent(
            greeting = "Good Morning",
            userName = "John",
            userBio = "Software Engineer",
            userPhotoUrl = null,
            currentStreak = 5,
            todayTasks = listOf(Task(id = 1, title = "Mock Task", priority = Priority.HIGH)),
            pendingTaskCount = 1,
            todayStudyFormatted = "2h 30m",
            todayStudyMinutes = 150,
            nextExamTitle = "Math Final",
            daysUntilNextExam = 3,
            activeGoals = emptyList(),
            isLoading = false,
            showSyncPrompt = true,
            dailyQuote = "Focus on being productive instead of busy.",
            dailyQuoteAuthor = "Tim Ferriss",
            showReflectionPrompt = true,
            onTaskToggle = { _, _ -> },
            onNavigateToTasks = {},
            onNavigateToTimer = {},
            onNavigateToExams = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {},
            onNavigateToStats = {},
            onNavigateToCalendar = {},
            onNavigateToReflection = {},
            onNavigateToAchievements = {}
        )
    }
}
