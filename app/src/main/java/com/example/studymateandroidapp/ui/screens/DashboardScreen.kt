package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.studymateandroidapp.viewmodel.DashboardViewModel.GoalSummary
import com.example.studymateandroidapp.ui.components.DailyQuoteCard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToExamDetails: (Long) -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReflection: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        upcomingExams = uiState.upcomingExams,
        activeGoals = uiState.activeGoals,
        totalGoalCount = uiState.totalGoalCount,
        completedGoalCount = uiState.completedGoalCount,
        isLoading = uiState.isLoading,
        showSyncPrompt = uiState.showSyncPrompt,
        dailyQuote = uiState.dailyQuote,
        dailyQuoteAuthor = uiState.dailyQuoteAuthor,
        showReflectionPrompt = uiState.showReflectionPrompt,
        onTaskToggle = { id, completed -> viewModel.onTaskCompletionToggled(id, completed) },
        onNavigateToTasks = onNavigateToTasks,
        onNavigateToTimer = onNavigateToTimer,
        onNavigateToExamDetails = onNavigateToExamDetails,
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
    upcomingExams: List<DashboardViewModel.ExamCountdown>,
    activeGoals: List<GoalSummary>,
    totalGoalCount: Int,
    completedGoalCount: Int,
    isLoading: Boolean,
    showSyncPrompt: Boolean,
    dailyQuote: String,
    dailyQuoteAuthor: String,
    showReflectionPrompt: Boolean,
    onTaskToggle: (Long, Boolean) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToExamDetails: (Long) -> Unit,
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
                        Icon(
                            painter = painterResource(id = R.drawable.achievements),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Achievements",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(
                            painter = painterResource(id = R.drawable.statistics),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Statistics",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCalendar,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", modifier = Modifier.size(35.dp))
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
                Spacer(Modifier.height(10.dp))
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
                            text = "Hi, $userName 👋",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (currentStreak > 0) "🔥 $currentStreak Day Streak" else "Start your streak today 🔥",
                            fontSize = 10.sp,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userBio,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
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

            // ── 4. Exam Countdown Cards ──────────────────────
            if (upcomingExams.isNotEmpty()) {
                item {
                    Text(
                        "Upcoming Exams",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(upcomingExams) { countdown ->
                            ExamCountdownCard(
                                countdown = countdown,
                                onClick = {
                                    onNavigateToExamDetails(countdown.id)
                                }
                            )
                        }
                    }
                }
            }

            // ── 5. Goals Card ───────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Goals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            
                            if (totalGoalCount == 0) {
                                Text("No goals yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text("Create your first goal to start tracking progress", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            } else {
                                Text("${activeGoals.size} active goals", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$completedGoalCount of $totalGoalCount completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                
                                if (activeGoals.isNotEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    activeGoals.take(2).forEach { goal ->
                                        GoalProgressRow(goal.title, goal.progressPercent)
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            Text("View Goals →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onNavigateToGoals() })
                        }
                        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                        val imageRes = if (isDark) {
                            R.drawable.goal_dark
                        } else {
                            R.drawable.goal
                        }

                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(start = 16.dp)
                        )
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
private fun GoalProgressRow(label: String, progressPercent: Int) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text("$progressPercent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun AestheticTaskRow(
    task: Task,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Checkbox
            Surface(
                modifier = Modifier.size(22.dp),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(
                    2.dp,
                    if (task.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                ),
                color =
                    if (task.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface
            ) {
                if (task.isCompleted) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration =
                        if (task.isCompleted)
                            TextDecoration.LineThrough
                        else
                            null
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (!task.subjectTag.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(task.tagColor.toULong())
                        ) {
                            Text(
                                text = task.subjectTag.uppercase(),
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    if (task.dueTime != null) {

                        Icon(
                            painter = painterResource(R.drawable.time),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = task.dueTime.format(
                                java.time.format.DateTimeFormatter.ofPattern(
                                    "h:mm a"
                                )
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamCountdownCard(
    countdown: DashboardViewModel.ExamCountdown,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(230.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Surface(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(50.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = when {
                            countdown.isToday -> "HAPPENING TODAY"
                            countdown.daysUntil == 1L -> "DUE TOMORROW"
                            else -> "STARTS IN ${countdown.daysUntil} DAYS"
                        },
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(text = countdown.title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(countdown.subject, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Spacer(Modifier.height(16.dp))
            Surface(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(8.dp)) {
                Text(
                    "View Details",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
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
            upcomingExams = listOf(
                DashboardViewModel.ExamCountdown(1, "Math Final", "Mathematics", 3, false),
                DashboardViewModel.ExamCountdown(2, "Physics Midterm", "Physics", 0, true)
            ),
            activeGoals = emptyList(),
            totalGoalCount = 0,
            completedGoalCount = 0,
            isLoading = false,
            showSyncPrompt = true,
            dailyQuote = "Focus on being productive instead of busy.",
            dailyQuoteAuthor = "Tim Ferriss",
            showReflectionPrompt = true,
            onTaskToggle = { _, _ -> },
            onNavigateToTasks = {},
            onNavigateToTimer = {},
            onNavigateToExamDetails = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {},
            onNavigateToStats = {},
            onNavigateToCalendar = {},
            onNavigateToReflection = {},
            onNavigateToAchievements = {}
        )
    }
}
