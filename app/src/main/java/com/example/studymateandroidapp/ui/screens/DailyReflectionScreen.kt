package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.studymateandroidapp.viewmodel.MotivationViewModel

@Composable
fun DailyReflectionScreen(
    viewModel: MotivationViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isReflectionSaved) {
        if (uiState.isReflectionSaved) {
            onNavigateBack()
            viewModel.resetReflectionSaved()
        }
    }

    DailyReflectionContent(
        onBack = onNavigateBack,
        onSaveReflection = { mood, reflection, highlight ->
            viewModel.onReflectionMoodChanged(mood)
            viewModel.onReflectionContentChanged(reflection)
            viewModel.onReflectionHighlightChanged(highlight)
            viewModel.saveReflection()
        }
    )
}

@Composable
fun DailyReflectionContent(
    onBack: () -> Unit,
    onSaveReflection: (String, String, String) -> Unit
) {
    var reflection by remember { mutableStateOf("") }
    var highlight by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Daily Reflection",
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Date Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF5F5F5),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Today", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Friday, May 8", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                    }
                    Icon(
                        painter = painterResource(R.drawable.reflection),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("How are you feeling?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("😊", "😴", "🙂", "💪", "🤯", "😭").forEach { mood ->
                    MoodEmoji(
                        emoji = mood,
                        isSelected = selectedMood == mood,
                        onClick = { selectedMood = mood }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("What did you study today?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = reflection,
                onValueChange = { reflection = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("Write freely... what went well, what you learned?") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("⭐ Study highlight of the day", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = highlight,
                onValueChange = { highlight = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Finally understood recursion!") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSaveReflection(selectedMood, reflection, highlight) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(painter = painterResource(R.drawable.save), contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Save Reflection", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MoodEmoji(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color.Black else Color.Transparent)
            .border(1.dp, if (isSelected) Color.Black else Color.LightGray, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 24.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DailyReflectionPreview() {
    MaterialTheme {
        DailyReflectionContent(onBack = {}, onSaveReflection = { _, _, _ -> })
    }
}
