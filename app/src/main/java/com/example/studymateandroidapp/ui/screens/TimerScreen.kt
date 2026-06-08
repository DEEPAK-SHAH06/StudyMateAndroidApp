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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
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
import com.example.studymateandroidapp.data.model.StudySession
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.TimerViewmodel
import java.time.format.DateTimeFormatter

@Composable
fun TimerScreen(
    viewModel: TimerViewmodel,
    examId: Long? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    TimerContent(
        uiState = uiState,
        onModeChange = { viewModel.setMode(it) },
        onPhaseChange = { viewModel.setPhase(it) },
        onTitleChange = { viewModel.updateStudyTitle(it) },
        onStart = { viewModel.startTimer() },
        onStop = { viewModel.stopTimer() },
        onReset = { viewModel.resetTimer() }
    )
}

@Composable
fun TimerContent(
    uiState: TimerViewmodel.TimerUiState,
    onModeChange: (TimerViewmodel.TimerMode) -> Unit,
    onPhaseChange: (TimerViewmodel.PomodoroPhase) -> Unit,
    onTitleChange: (String) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Study Timer",
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(painter = painterResource(id = R.drawable.statistics), contentDescription = "Stats")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Pomodoro / Stop watch Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(if (uiState.mode == TimerViewmodel.TimerMode.POMODORO) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onModeChange(TimerViewmodel.TimerMode.POMODORO) },
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
                            .clickable { onModeChange(TimerViewmodel.TimerMode.STOPWATCH) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Work / Break / Long
                if (uiState.mode == TimerViewmodel.TimerMode.POMODORO) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimerModeChip("Work", isSelected = uiState.phase == TimerViewmodel.PomodoroPhase.WORK, modifier = Modifier.weight(1f)) { onPhaseChange(TimerViewmodel.PomodoroPhase.WORK) }
                        TimerModeChip("Break", isSelected = uiState.phase == TimerViewmodel.PomodoroPhase.BREAK, modifier = Modifier.weight(1f)) { onPhaseChange(TimerViewmodel.PomodoroPhase.BREAK) }
                        TimerModeChip("Long", isSelected = uiState.phase == TimerViewmodel.PomodoroPhase.LONG_BREAK, modifier = Modifier.weight(1f)) { onPhaseChange(TimerViewmodel.PomodoroPhase.LONG_BREAK) }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Circular Timer
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .border(8.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
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

                Spacer(modifier = Modifier.height(32.dp))

                // Input Field
                OutlinedTextField(
                    value = uiState.studyTitle,
                    onValueChange = onTitleChange,
                    placeholder = { Text("What are you studying?", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = !uiState.isRunning
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!uiState.isRunning) {
                        Button(
                            onClick = onStart,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        Button(
                            onClick = onStop,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onError)
                        }
                    }

                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
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
                            val totalMin = uiState.totalSecondsWorkedToday / 60
                            val h = totalMin / 60
                            val m = totalMin % 60
                            Text("Today's Study Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Text("${h}h ${m}m", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recent Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(uiState.recentSessions) { session ->
                RecentSessionItem(
                    title = session.subject,
                    duration = "${session.durationMinutes} min",
                    date = session.startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TimerModeChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
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
fun RecentSessionItem(title: String, duration: String, date: String) {
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
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Text(text = duration, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
//            Column {
//                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
//                Text(text = duration, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//            }
//            Text(text = date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    MaterialTheme {
        TimerContent(
            uiState = TimerViewmodel.TimerUiState(),
            onModeChange = {},
            onPhaseChange = {},
            onTitleChange = {},
            onStart = {},
            onStop = {},
            onReset = {}
        )
    }
}
