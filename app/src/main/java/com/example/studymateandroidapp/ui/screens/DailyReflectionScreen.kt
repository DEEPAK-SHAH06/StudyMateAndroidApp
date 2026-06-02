package com.example.studymateandroidapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.studymateandroidapp.data.model.DailyReflection
import com.example.studymateandroidapp.ui.components.AppCard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.ui.theme.*
import com.example.studymateandroidapp.viewmodel.MotivationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyReflectionScreen(
    viewModel: MotivationViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState         by viewModel.uiState.collectAsState()
    val recentReflections by viewModel.recentReflections.collectAsState()

    // Navigate back only on first save, not every time state changes
    LaunchedEffect(uiState.isReflectionSaved) {
        if (uiState.isReflectionSaved) {
            viewModel.resetReflectionSaved()
            // Don't pop — let the user see the updated "Update Reflection" button
        }
    }

    DailyReflectionContent(
        content           = uiState.reflectionContent,
        mood              = uiState.reflectionMood,
        highlight         = uiState.reflectionHighlight,
        alreadySaved      = uiState.todayReflection != null,
        recentReflections = recentReflections,
        onBack            = onNavigateBack,
        onContentChange   = viewModel::onReflectionContentChanged,
        onMoodChange      = viewModel::onReflectionMoodChanged,
        onHighlightChange = viewModel::onReflectionHighlightChanged,
        onSave            = viewModel::saveReflection,
        onEditReflection  = viewModel::editReflection,
        onDeleteReflection = viewModel::deleteReflection
    )
}

@Composable
fun DailyReflectionContent(
    content: String,
    mood: String,
    highlight: String,
    alreadySaved: Boolean,
    recentReflections: List<DailyReflection>,
    onBack: () -> Unit,
    onContentChange: (String) -> Unit,
    onMoodChange: (String) -> Unit,
    onHighlightChange: (String) -> Unit,
    onSave: () -> Unit,
    onEditReflection: (DailyReflection) -> Unit,
    onDeleteReflection: (DailyReflection) -> Unit
) {
    val moods = listOf("😊", "😴", "🙂", "💪", "🤯", "😭")
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            StudyMateTopBar(title = "Daily Reflection", onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundWhite)
        ) {
            // ── Tabs ──────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = BackgroundWhite
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text = {
                        Text("Today", color = if (selectedTab == 0) PureBlack else TextGray)
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text = {
                        Text("History", color = if (selectedTab == 1) PureBlack else TextGray)
                    }
                )
            }

            AnimatedContent(targetState = selectedTab, label = "reflection_tabs") { tab ->
                if (tab == 0) {
                    TodayTab(
                        content         = content,
                        mood            = mood,
                        highlight       = highlight,
                        moods           = moods,
                        alreadySaved    = alreadySaved,
                        onContentChange = onContentChange,
                        onMoodChange    = onMoodChange,
                        onHighlightChange = onHighlightChange,
                        onSave          = onSave
                    )
                } else {
                    HistoryTab(
                        reflections = recentReflections,
                        onEditReflection = {
                            onEditReflection(it)
                            selectedTab = 0
                        },
                        onDeleteReflection = onDeleteReflection
                    )
                }
            }
        }
    }
}

// ── Today Tab ─────────────────────────────────────────────

@Composable
private fun TodayTab(
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
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Date card
        item {
            val today = LocalDate.now()
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            today.format(DateTimeFormatter.ofPattern("EEEE")),
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                            color = SoftRed
                        )
                    }
                    Icon(
                        painter            = painterResource(R.drawable.reflection),
                        contentDescription = null,
                        modifier           = Modifier.size(70.dp),
                        tint               = Color.Unspecified
                    )
                }
            }
        }

        // Mood picker
        item {
            Text("How are you feeling?", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                moods.forEach { m ->
                    MoodEmoji(
                        emoji      = m,
                        isSelected = mood == m,
                        onClick    = { onMoodChange(m) }
                    )
                }
            }
        }

        // Study notes
        item {
            Text("What did you study today?", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = content,
                onValueChange = onContentChange,
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder   = { Text("Write freely... what went well, what you learned, any questions?") },
                shape         = RoundedCornerShape(16.dp)
            )
        }

        // Study highlight
        item {
            Text("⭐ Study highlight of the day", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = highlight,
                onValueChange = onHighlightChange,
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("e.g. Finally understood recursion!") },
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp)
            )
        }

        // Save button
        item {
            Button(
                onClick  = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PureBlack),
                shape  = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    painter            = painterResource(R.drawable.save),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (alreadySaved) "Update Reflection" else "Save Reflection")
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── History Tab ───────────────────────────────────────────

@Composable
private fun HistoryTab(
    reflections: List<DailyReflection>,
    onEditReflection: (DailyReflection) -> Unit,
    onDeleteReflection: (DailyReflection) -> Unit
) {
    if (reflections.isEmpty()) {
        Box(
            modifier          = Modifier.fillMaxSize(),
            contentAlignment  = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌱", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text("No reflections yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Start journaling today!", style = MaterialTheme.typography.bodyMedium, color = TextGray)
            }
        }
        return
    }

    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reflections) { reflection ->
            ReflectionHistoryCard(
                reflection = reflection,
                onEdit = { onEditReflection(reflection) },
                onDelete = { onDeleteReflection(reflection) }
            )
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Reusable components ───────────────────────────────────

@Composable
fun MoodEmoji(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(
                width  = 2.dp,
                color  = if (isSelected) PureBlack else Color.LightGray,
                shape  = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 24.sp)
    }
}

@Composable
fun ReflectionHistoryCard(
    reflection: DailyReflection,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val date = LocalDate.ofEpochDay(reflection.date)

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        date.format(DateTimeFormatter.ofPattern("EEEE")),
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        fontSize = 10.sp,
                        color    = TextGray
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(reflection.mood, fontSize = 24.sp)
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit reflection")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete reflection")
                    }
                }
            }

            if (reflection.content.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    reflection.content,
                    style    = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
            }

            if (reflection.studyHighlight.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF9C4))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐ ", fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            reflection.studyHighlight,
                            style      = MaterialTheme.typography.labelMedium,
                            color      = Color(0xFF5D4037),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyReflectionPreview() {
    MaterialTheme {
        DailyReflectionContent(
            content           = "",
            mood              = "😊",
            highlight         = "",
            alreadySaved      = false,
            recentReflections = emptyList(),
            onBack            = {},
            onContentChange   = {},
            onMoodChange      = {},
            onHighlightChange = {},
            onSave            = {},
            onEditReflection  = {},
            onDeleteReflection = {}
        )
    }
}
