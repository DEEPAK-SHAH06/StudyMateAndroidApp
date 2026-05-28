package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.StatisticsViewmodel

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewmodel,
    onBack: () -> Unit
) {
    StatisticsContent(onBack = onBack)
}

@Composable
fun StatisticsContent(
    onBack: () -> Unit
) {
    // Mock data for preview/now
    val totalTasks = 8
    val completedTasks = 4
    val taskPercentage = 50
    val todayStudy = "2h 5m"
    val goalsMet = 2
    val weeklyStudy = "14h 30m"
    val averageStudy = "2h 4m/day"

    val weeklyStudyMinutes = listOf(20, 45, 70, 10, 90, 30, 50)
    val labels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val maxMinutes = weeklyStudyMinutes.maxOrNull() ?: 1

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Statistics",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.task_done),
                    overview = "$completedTasks/$totalTasks",
                    description = "Tasks Done",
                    subdescription = "$taskPercentage%"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.total),
                    overview = todayStudy,
                    description = "Today's Study",
                    subdescription = ""
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.task_done),
                    overview = "$goalsMet",
                    description = "Goals Met",
                    subdescription = ""
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.total),
                    overview = weeklyStudy,
                    description = "This Week",
                    subdescription = "Avg: $averageStudy"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Weekly Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFEAEAEA)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    labels.forEachIndexed { index, label ->
                        val fillHeight = (weeklyStudyMinutes[index] * 100) / maxMinutes
                        WeeklyBar(fillHeight = fillHeight, label = label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    overview: String,
    description: String,
    subdescription: String
) {
    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(painter = painter, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(overview, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            if (subdescription.isNotEmpty()) {
                Text(subdescription, color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun WeeklyBar(fillHeight: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(120.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fillHeight.dp)
                    .background(Color.Black, shape = RoundedCornerShape(12.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.Black, fontSize = 10.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    MaterialTheme {
        StatisticsContent(onBack = {})
    }
}
