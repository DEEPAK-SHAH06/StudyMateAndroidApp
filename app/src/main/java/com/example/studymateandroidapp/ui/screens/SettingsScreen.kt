package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
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
import com.example.studymateandroidapp.viewmodel.SettingViewmodel

@Composable
fun SettingsScreen(
    viewModel: SettingViewmodel,
    onBack: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    SettingsContent(
        onBack = onBack,
        onStatsClick = onNavigateToStats,
        onAchievementsClick = onNavigateToAchievements,
        onEditProfileClick = onNavigateToEditProfile
    )
}

@Composable
fun SettingsContent(
    onBack: () -> Unit,
    onStatsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    var taskNotif by remember { mutableStateOf(true) }
    var examNotif by remember { mutableStateOf(true) }
    var habitNotif by remember { mutableStateOf(true) }
    var focusMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Settings",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onAchievementsClick) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements")
                    }
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                    }
                }
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

            // Profile Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Username",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sync Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Not Synced", fontWeight = FontWeight.Bold)
                            Text("Sign in to keep your data safe", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Button(
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Sign In", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditProfileClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Your Profile")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationSettingItem(
                title = "Task Reminders",
                subtitle = "Get notified before task deadline",
                checked = taskNotif,
                onCheckedChange = { taskNotif = it }
            )

            NotificationSettingItem(
                title = "Exam Alerts",
                subtitle = "Get alerts 1 and 3 days before exams",
                checked = examNotif,
                onCheckedChange = { examNotif = it }
            )

            NotificationSettingItem(
                title = "Daily Study Habit",
                subtitle = "Daily reminder to keep up your study habit",
                checked = habitNotif,
                onCheckedChange = { habitNotif = it }
            )

            NotificationSettingItem(
                title = "Focus Mode",
                subtitle = "Pause all notifications during focus time",
                checked = focusMode,
                onCheckedChange = { focusMode = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NotificationSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color.Black)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MaterialTheme {
        SettingsContent(
            onBack = {},
            onStatsClick = {},
            onAchievementsClick = {},
            onEditProfileClick = {}
        )
    }
}
