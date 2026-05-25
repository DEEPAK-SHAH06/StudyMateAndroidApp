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
            CircularProgressIndicator(color = Color.Black)
        }
        return
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            StudyMateTopBar(
                title = "StudyMate",
                actions = {
                    IconButton(onClick = onNavigateToAchievements) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements")
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            //contentPadding = PaddingValues(horizontal = 24.dp, bottom = 32.dp)
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            // ── 1. Profile Header ────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = greeting.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Hi, $userName",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userBio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
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
                        color = Color(0xFFFFE0B2).copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Keep your data safe", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                Text("Sign in with Google to enable auto-sync and backup.", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE65100).copy(alpha = 0.8f))
                            }
                            Icon(Icons.Default.CloudSync, null, tint = Color(0xFFE65100), modifier = Modifier.size(32.dp))
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
                            .background(Brush.verticalGradient(colors = listOf(Color(0xFF2D2D2D), Color(0xFF5A5A5A))))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Surface(color = Color.White, shape = RoundedCornerShape(50.dp)) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, null, Modifier.size(14.dp), tint = Color.Black)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = when {
                                            daysUntilNextExam == 0L -> "HAPPENING TODAY"
                                            daysUntilNextExam == 1L -> "DUE TOMORROW"
                                            else -> "STARTS IN $daysUntilNextExam DAYS"
                                        },
                                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(text = nextExamTitle, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Preparing for your next big challenge.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToTimer,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Start Session", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ── 5. Focus Pulse ───────────────────────────
            item {
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = Color(0xFFF5F5F5)) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Focus Pulse", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Total focus: $todayStudyFormatted today", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            FocusStatRow(Icons.Default.FlashOn, "Deep Work", "${(todayStudyMinutes * 0.6).toInt()}m")
                            FocusStatRow(Icons.Default.MenuBook, "Study", "${(todayStudyMinutes * 0.4).toInt()}m")
                            
                            Spacer(Modifier.height(12.dp))
                            Text("Full insights →", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { onNavigateToStats() })
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── 6. Today's Plan ────────────────────
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Today's Plan", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    TextButton(onClick = onNavigateToTasks) { Text("View All") }
                }
            }

            if (todayTasks.isEmpty()) {
                item {
                    Text("No tasks for today!", modifier = Modifier.padding(16.dp), color = Color.Gray)
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
                        color = Color(0xFFE3F2FD),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2196F3))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RateReview, null, tint = Color(0xFF2196F3))
                            Spacer(Modifier.width(16.dp))
                            Text("Evening Reflection", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, Modifier.size(16.dp))
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
        Icon(icon, null, Modifier.size(14.dp), tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp)
        Spacer(Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            tint = if (task.isCompleted) Color(0xFF4CAF50) else Color.Gray
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            Text(task.priority.name, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
