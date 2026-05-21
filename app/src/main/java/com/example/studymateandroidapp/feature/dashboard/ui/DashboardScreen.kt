package com.example.studymateandroidapp.feature.dashboard.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.studymateandroidapp.core.model.Task
import com.example.studymateandroidapp.feature.dashboard.viewmodel.DashboardViewModel
import com.example.studymateandroidapp.feature.motivation.ui.CelebrationOverlay
import com.example.studymateandroidapp.feature.dashboard.viewmodel.DashboardViewModel.GoalSummary
import com.example.studymateandroidapp.feature.motivation.ui.DailyQuoteCard

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
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Top Bar ────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateToAchievements) {
                        Icon(Icons.Default.EmojiEvents, "Achievements", Modifier.size(32.dp))
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.BarChart, "Statistics", Modifier.size(32.dp))
                    }
                }
            }

            // ── 1. Profile Header ────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        AsyncImage(
                            model = "file:///Users/deepakshah/Desktop/TestStudyPlanner/app/src/main/assets/ui_images/user_avatar.png",
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
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
                Spacer(Modifier.height(24.dp))
            }

            // ── 2. Daily Inspiration Quote ──────────────────
            if (dailyQuote.isNotBlank()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        DailyQuoteCard(quote = dailyQuote, author = dailyQuoteAuthor)
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── 3. Sync Card (Keep your data safe) ──────────
            if (showSyncPrompt) {
                item {
                    Surface(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFFFE0B2).copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Keep your data safe",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    "Sign in with Google to enable auto-sync and backup.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE65100).copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(32.dp)
                            )
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
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF2D2D2D), Color(0xFF5A5A5A))
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(32.dp)) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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
                            Text(
                                text = nextExamTitle,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Preparing for your next big challenge.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = onNavigateToTimer,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("Start Session", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ── 5. Focus Pulse Card ───────────────────────────
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFFF1F1F1)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Focus Pulse",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Total focus: $todayStudyFormatted today",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(16.dp))
                            // Subject-based breakdown for stats
                            val stats = listOf(
                                Triple(Icons.Default.FlashOn, "Deep Work", "${(todayStudyMinutes * 0.6).toInt()}m"),
                                Triple(Icons.Default.Eco, "Breaks", "${(todayStudyMinutes * 0.1).toInt()}m"),
                                Triple(Icons.Default.MenuBook, "Study", "${(todayStudyMinutes * 0.3).toInt()}m")
                            )
                            stats.forEach { (icon, label, value) ->
                                FocusStatRow(icon, label, value)
                            }

                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onNavigateToStats() }) {
                                Text("Full insights", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, null, Modifier.size(12.dp))
                            }
                        }
                        AsyncImage(
                            model = "file:///Users/deepakshah/Desktop/TestStudyPlanner/app/src/main/assets/ui_images/reading_illustration.png",
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── 6. Today's Plan (Tasks) ────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Today's Plan", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    TextButton(onClick = onNavigateToTasks) {
                        Text("View All", fontSize = 14.sp)
                    }
                }
            }

            if (todayTasks.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "No tasks left for today! Relax or start something new.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(24.dp),
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(todayTasks.take(3)) { task ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        AestheticTaskRow(
                            task = task,
                            onToggle = { onTaskToggle(task.id, !task.isCompleted) }
                        )
                    }
                }
            }

            // ── Evening Reflection Card ────────────────────
            if (showReflectionPrompt) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Surface(
                        onClick = onNavigateToReflection,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(72.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3)),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.RateReview, null, Modifier.size(32.dp), tint = Color.Black)
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Evening Reflection", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("What did you study today?", color = Color.Gray, fontSize = 12.sp)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, Modifier.size(24.dp), tint = Color.Black)
                        }
                    }
                }
            }

            // ── Calendar Shortcut ────────────────────────
            item {
                Spacer(Modifier.height(32.dp))
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Surface(
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CalendarMonth, "Calendar", Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardPreview() {
    DashboardContent( greeting = "Good Morning", userName = "John", userBio = "Software Engineer", todayTasks = emptyList(), pendingTaskCount = 0, todayStudyFormatted = "00:00", todayStudyMinutes = 0, nextExamTitle = null, daysUntilNextExam = null, activeGoals = emptyList(), isLoading = false, showSyncPrompt = false, dailyQuote = "", dailyQuoteAuthor = "", showReflectionPrompt = false, onTaskToggle = { _, _ -> }, onNavigateToTasks = {}, onNavigateToTimer = {}, onNavigateToExams = {}, onNavigateToGoals = {}, onNavigateToSettings = {}, onNavigateToStats = {}, onNavigateToCalendar = {}, onNavigateToReflection = {}, onNavigateToAchievements = {})
}

@Composable
private fun FocusStatRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, Modifier.size(14.dp), tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Preview
@Composable
private fun FocusStatRowPreview() {
    FocusStatRow(icon = Icons.Default.FlashOn, label = "Deep Work", value = "30m")
}


@Composable
private fun AestheticTaskRow(task: Task, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle
                else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isCompleted) Color(0xFF4CAF50)
                else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold,
                    color = if (task.isCompleted) Color.Gray else Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task.priority.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
@Preview
@Composable
private fun AstheticRowView(){
    //AestheticTaskRow(task = Task(id = 1, title = "Study for exam", priority = Task.Priority.HIGH), onToggle = {})
}
