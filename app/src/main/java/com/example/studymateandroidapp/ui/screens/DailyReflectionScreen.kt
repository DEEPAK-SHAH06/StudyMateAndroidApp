package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import com.example.studymateandroidapp.ui.components.AppCard
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.ui.theme.*
import com.example.studymateandroidapp.viewmodel.MotivationViewModel

data class ReflectionData(
    val day: String,
    val date: String,
    val mood: String,
    val subject: String,
    val highlight: String
)

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
    // Reflection input states
    var reflection by remember { mutableStateOf("") }
    var highlight by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }

    // Tab state
    var selectedTab by remember { mutableStateOf(0) }

    // Fake history data for now
    val reflectionList = listOf(
        ReflectionData(
            day = "Wednesday",
            date = "Apr 29, 2026",
            mood = "💪",
            subject = "Python",
            highlight = "Learned some basics"
        ),

        ReflectionData(
            day = "Thursday",
            date = "Apr 20, 2026",
            mood = "🙂",
            subject = "Java",
            highlight = "Made more notes about Java"
        ),

        ReflectionData(
            day = "Friday",
            date = "Apr 22, 2026",
            mood = "😴",
            subject = "DSA",
            highlight = "Rest and sleep"
        )
    )

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
                .background(BackgroundWhite)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundWhite
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {  selectedTab = 0 },
                    text = {
                        Text(
                            "Today",
                            color = if (selectedTab == 0) PureBlack else TextGray
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "History",
                            color = if (selectedTab == 1) PureBlack else TextGray
                        )
                    }
                )
            }

            // TODAY TAB
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Card
                    AppCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    "Thursday",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "April 30, 2026",
                                    color = SoftRed
                                )
                            }

                            Icon(
                                painter = painterResource(R.drawable.reflection),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "How are you feeling?",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        listOf("😊", "😴", "🙂", "💪", "🤯", "😭").forEach { mood ->

                            MoodEmoji(
                                emoji = mood,
                                isSelected = selectedMood == mood,
                                onClick = {
                                    selectedMood = mood
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "What did you study today?",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = reflection,
                        onValueChange = { reflection = it },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),

                        placeholder = {
                            Text(
                                "Write freely... what went well, what you learned?"
                            )
                        },

                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "⭐ Study highlight of the day",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = highlight,
                        onValueChange = { highlight = it },

                        modifier = Modifier.fillMaxWidth(),

                        placeholder = {
                            Text(
                                "e.g. Finally understood recursion!"
                            )
                        },

                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            onSaveReflection(
                                selectedMood,
                                reflection,
                                highlight
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureBlack
                        ),

                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Save Reflection")
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // HISTORY TAB
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {

                    items(reflectionList.size) { index ->

                        val reflection = reflectionList[index]

                        ReflectionHistoryCard(
                            day = reflection.day,
                            date = reflection.date,
                            mood = reflection.mood,
                            subject = reflection.subject,
                            highlight = reflection.highlight
                        )
                    }
                }
            }
        }
    }
}

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
                width = 2.dp,
                color = if (isSelected) Color.Black else Color.LightGray,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
    }
}

@Composable
fun ReflectionHistoryCard(
    day : String,
    date: String,
    mood: String,
    subject : String,
    highlight : String
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    Text(day,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Text(date,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.Gray
                    )
                }

                Text(mood,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(subject,
                fontSize = 15.sp)

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier.border(
                    1.dp,
                    Color.LightGray.copy(0.5f),
                    RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(0.2f))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐")

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(highlight,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyReflectionPreview() {
    MaterialTheme {
        DailyReflectionContent(onBack = {}, onSaveReflection = { _, _, _ -> })
    }
}
