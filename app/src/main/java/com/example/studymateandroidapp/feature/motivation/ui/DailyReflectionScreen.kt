package com.example.studymateandroidapp.feature.motivation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyplanner.core.model.DailyReflection
import com.studyplanner.feature.motivation.viewmodel.MotivationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReflectionScreen(
    viewModel: MotivationViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reflections by viewModel.recentReflections.collectAsStateWithLifecycle()

    val moods = listOf("😊", "😐", "😴", "🔥", "💪", "😤", "🥹", "😎")
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.isReflectionSaved) {
        if (uiState.isReflectionSaved) viewModel.resetReflectionSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Reflection", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF6750A4)
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Today", modifier = Modifier.padding(vertical = 14.dp), fontWeight = FontWeight.SemiBold)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("History", modifier = Modifier.padding(vertical = 14.dp), fontWeight = FontWeight.SemiBold)
                }
            }

            AnimatedContent(targetState = selectedTab, label = "tabs") { tab ->
                if (tab == 0) {
                    TodayReflectionTab(
                        content = uiState.reflectionContent,
                        mood = uiState.reflectionMood,
                        highlight = uiState.reflectionHighlight,
                        moods = moods,
                        alreadySaved = uiState.todayReflection != null,
                        onContentChange = viewModel::onReflectionContentChanged,
                        onMoodChange = viewModel::onReflectionMoodChanged,
                        onHighlightChange = viewModel::onReflectionHighlightChanged,
                        onSave = viewModel::saveReflection
                    )
                } else {
                    ReflectionHistoryTab(reflections = reflections)
                }
            }
        }
    }
}

@Composable
private fun TodayReflectionTab(
    content: String,
    mood: String,
    highlight: String,
    moods: List<String>,
    alreadySaved: Boolean,
    onContentChange: (String) -> Unit,
    onMoodChange: (String) -> Unit,
    onHighlightChange: (String) -> Unit,
    onSave: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // Date header
            val today = LocalDate.now()
            Column {
                Text(
                    today.format(DateTimeFormatter.ofPattern("EEEE")),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // Mood selector
        item {
            Text("How are you feeling?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moods.forEach { m ->
                    val selected = m == mood
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFF6750A4).copy(0.15f) else Color.Transparent)
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) Color(0xFF6750A4) else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { onMoodChange(m) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(m, fontSize = 22.sp)
                    }
                }
            }
        }

        // Main reflection area
        item {
            Text("What did you study today?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                placeholder = { Text("Write freely... what went well, what you learned, any questions?", color = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6750A4),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        // Highlight of the day
        item {
            Text("⭐ Study highlight of the day", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = highlight,
                onValueChange = onHighlightChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Finally understood quadratic equations!", color = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6750A4),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        item {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (alreadySaved) "Update Reflection" else "Save Reflection",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun ReflectionHistoryTab(reflections: List<DailyReflection>) {
    if (reflections.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌱", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text("No reflections yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Start journaling today!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        items(reflections) { reflection ->
            ReflectionCard(reflection)
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun ReflectionCard(reflection: DailyReflection) {
    val date = LocalDate.ofEpochDay(reflection.date)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        date.format(DateTimeFormatter.ofPattern("EEEE")),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Text(reflection.mood, fontSize = 28.sp)
            }
            if (reflection.content.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    reflection.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF333333),
                    maxLines = 3
                )
            }
            if (reflection.studyHighlight.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF9C4))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐ ", fontSize = 14.sp)
                    Text(
                        reflection.studyHighlight,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF5D4037),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
