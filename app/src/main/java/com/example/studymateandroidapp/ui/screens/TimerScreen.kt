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
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.TimerViewmodel

@Composable
fun TimerScreen(
    viewModel: TimerViewmodel,
    examId: Long? = null
) {
    // For now, since ViewModel is empty, we use local state or placeholder
    TimerContent(
        examId = examId,
        onStartSession = { /* TODO */ },
        onStopSession = { /* TODO */ }
    )
}

@Composable
fun TimerContent(
    examId: Long? = null,
    onStartSession: () -> Unit,
    onStopSession: () -> Unit
) {
    var timerMode by remember { mutableStateOf("Pomodoro") }
    var subMode by remember { mutableStateOf("Work") }
    var studyText by remember { mutableStateOf("") }

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
                        .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(if (timerMode == "Pomodoro") Color.Black else Color.Transparent)
                            .clickable { timerMode = "Pomodoro" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pomodoro",
                            color = if (timerMode == "Pomodoro") Color.White else Color.Black,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(if (timerMode == "Stopwatch") Color.Black else Color.Transparent)
                            .clickable { timerMode = "Stopwatch" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Stopwatch",
                            color = if (timerMode == "Stopwatch") Color.White else Color.Black,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Work / Break / Long
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimerModeChip("Work", isSelected = subMode == "Work", modifier = Modifier.weight(1f)) { subMode = "Work" }
                    TimerModeChip("Break", isSelected = subMode == "Break", modifier = Modifier.weight(1f)) { subMode = "Break" }
                    TimerModeChip("Long", isSelected = subMode == "Long", modifier = Modifier.weight(1f)) { subMode = "Long" }
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
                            .border(8.dp, Color(0xFFF2F2F2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "25:00",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Input Field
                OutlinedTextField(
                    value = studyText,
                    onValueChange = { studyText = it },
                    placeholder = { Text("What are you studying?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Start Session Button
                Button(
                    onClick = onStartSession,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Session", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Study Time Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.today_time),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Today's Study Time", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("1h 4m", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recent Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(listOf(
                Triple("Games", "3 min", "2024-04-11"),
                Triple("Mr. David Moral", "6 min", "2024-04-09"),
                Triple("Life more than", "8 min", "2024-04-08")
            )) { session ->
                RecentSessionItem(session.first, session.second, session.third)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TimerModeChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color.Black else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.Black,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun RecentSessionItem(title: String, duration: String, date: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8F8F8),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = duration, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(text = date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    MaterialTheme {
        TimerContent(
            onStartSession = {},
            onStopSession = {}
        )
    }
}
