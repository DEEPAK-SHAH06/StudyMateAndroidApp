package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.TimerViewmodel
import java.time.format.DateTimeFormatter

@Composable
fun TimerScreen(
    viewModel: TimerViewmodel,
    examId: Long? = null,
    onNavigateToAchievements: () -> Unit,
    onStatsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    TimerContent(
        uiState = uiState,
        onModeChange = { viewModel.setMode(it) },
        onPhaseChange = { viewModel.setPhase(it) },
        onStatsClick        = onStatsClick,
        onNavigateToAchievements = onNavigateToAchievements,
        onTitleChange = { viewModel.updateStudyTitle(it) },
        onStart = { viewModel.startTimer() },
        onPause = { viewModel.pauseTimer() },
        onResume = { viewModel.resumeTimer() },
        onStopAndSave = { viewModel.stopAndSave() },
        onReset = { viewModel.resetTimer() },
        onDeleteSession = { viewModel.deleteSession(it) }
    )
}

@Composable
fun TimerContent(
    uiState: TimerViewmodel.TimerUiState,
    onNavigateToAchievements: () -> Unit,
    onStatsClick: () -> Unit,
    onModeChange: (TimerViewmodel.TimerMode) -> Unit,
    onPhaseChange: (TimerViewmodel.PomodoroPhase) -> Unit,
    onTitleChange: (String) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStopAndSave: () -> Unit,
    onReset: () -> Unit,
    onDeleteSession: (com.example.studymateandroidapp.data.model.StudySession) -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "",
                actions = {
                    IconButton(onClick = onNavigateToAchievements) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            item {
                Text(
                    "Study Timer :",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Pomodoro / Stop watch Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(if (uiState.mode == TimerViewmodel.TimerMode.POMODORO) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable(enabled = !uiState.isRunning && !uiState.isPaused) { onModeChange(TimerViewmodel.TimerMode.POMODORO) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pomodoro",
                            color = if (uiState.mode == TimerViewmodel.TimerMode.POMODORO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(if (uiState.mode == TimerViewmodel.TimerMode.STOPWATCH) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable(enabled = !uiState.isRunning && !uiState.isPaused) { onModeChange(TimerViewmodel.TimerMode.STOPWATCH) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Stopwatch",
                            color = if (uiState.mode == TimerViewmodel.TimerMode.STOPWATCH) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Work / Break / Long
                if (uiState.mode == TimerViewmodel.TimerMode.POMODORO) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TimerModeChip("Work", isSelected = uiState.phase == TimerViewmodel.PomodoroPhase.WORK, enabled = !uiState.isRunning && !uiState.isPaused, modifier = Modifier.weight(1f)) { onPhaseChange(TimerViewmodel.PomodoroPhase.WORK) }
                        TimerModeChip("Break", isSelected = uiState.phase == TimerViewmodel.PomodoroPhase.BREAK, enabled = !uiState.isRunning && !uiState.isPaused, modifier = Modifier.weight(1f)) { onPhaseChange(TimerViewmodel.PomodoroPhase.BREAK) }
                        TimerModeChip("Long", isSelected = uiState.phase == TimerViewmodel.PomodoroPhase.LONG_BREAK, enabled = !uiState.isRunning && !uiState.isPaused, modifier = Modifier.weight(1f)) { onPhaseChange(TimerViewmodel.PomodoroPhase.LONG_BREAK) }
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                // Circular Timer
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(8.dp, if (uiState.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val minutes = uiState.timeLeftSeconds / 60
                        val seconds = uiState.timeLeftSeconds % 60
                        Text(
                            text = "%02d:%02d".format(minutes, seconds),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Input Field
                OutlinedTextField(
                    value = uiState.studyTitle,
                    onValueChange = onTitleChange,
                    placeholder = { Text("What are you studying?", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = !uiState.isRunning && !uiState.isPaused
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!uiState.isRunning && !uiState.isPaused) {
                        // START
                        Button(
                            onClick = onStart,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Session", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else if (uiState.isRunning) {
                        // PAUSE
                        Button(
                            onClick = onPause,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Pause, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pause", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
                        }
                    } else {
                        // RESUME
                        Button(
                            onClick = onResume,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Resume", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                    if (uiState.isRunning || uiState.isPaused) {
                        // STOP & SAVE
                        IconButton(
                            onClick = onStopAndSave,
                            modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Stop & Save", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }

                    // RESET
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Study Time Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.today_time),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Today's Study Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Text(formatDuration(uiState.totalSecondsWorkedToday), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recent Sessions",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(uiState.recentSessions) { session ->
                RecentSessionItem(
                    title = session.subject,
                    duration = formatDuration(session.durationSeconds),
                    date = session.startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    onDelete = { onDeleteSession(session) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TimerModeChip(label: String, isSelected: Boolean, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.clickable(enabled = enabled) { onClick() }.padding(start = 12.dp, end = 12.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun RecentSessionItem(title: String, duration: String, date: String, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = duration, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Session",
                        Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun formatDuration(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    
    return when {
        h > 0 -> "${h}h ${m}m ${s}s"
        m > 0 -> "${m}m ${s}s"
        else -> "${s}s"
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    MaterialTheme {
        TimerContent(
            uiState = TimerViewmodel.TimerUiState(),
            onModeChange = {},
            onPhaseChange = {},
            onTitleChange = {},
            onNavigateToAchievements = {},
            onStatsClick = {},
            onStart = {},
            onPause = {},
            onResume = {},
            onStopAndSave = {},
            onReset = {},
            onDeleteSession = {}
        )
    }
}
