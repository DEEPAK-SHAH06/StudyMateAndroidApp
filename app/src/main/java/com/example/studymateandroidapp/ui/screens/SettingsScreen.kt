package com.example.studymateandroidapp.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.model.ReminderType
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
    val settings by viewModel.settings.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val pendingReminderStates = remember { mutableStateMapOf<ReminderType, Boolean>() }

    LaunchedEffect(settings) {
        Log.d("SettingsScreen", "Loaded ${settings.size} reminder settings")
        settings.forEach { setting ->
            if (pendingReminderStates[setting.type] == setting.isEnabled) {
                pendingReminderStates.remove(setting.type)
            }
        }
    }

    val isSignedIn = currentUser != null
    val displayName =
        currentUser?.displayName
            ?: currentUser?.email
            ?: "Guest"

    SettingsContent(
        displayName         = displayName,
        isSignedIn          = isSignedIn,
        syncStatus          = syncStatus,
        reminderSettings    = settings.map { setting ->
            pendingReminderStates[setting.type]?.let { setting.copy(isEnabled = it) } ?: setting
        },
        onBack              = onBack,
        onStatsClick        = onNavigateToStats,
        onAchievementsClick = onNavigateToAchievements,
        onEditProfileClick  = onNavigateToEditProfile,
        onToggleReminder    = {
            Log.d("SettingsScreen", "Switch changed: type=${it.type}, enabled=${it.isEnabled}")
            pendingReminderStates[it.type] = it.isEnabled
            viewModel.toggleReminder(it)
        },
        onSignIn            = { viewModel.signInWithGoogle() },
        onSignOut           = { viewModel.signOut() }
    )
}

@Composable
fun SettingsContent(
    displayName: String,
    isSignedIn: Boolean,
    syncStatus: String,
    reminderSettings: List<ReminderSetting>,
    onBack: (() -> Unit)?,
    onStatsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onToggleReminder: (ReminderSetting) -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Settings",
                onBack = null,
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Profile Section ───────────────────────────
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
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Sync Card ─────────────────────────────
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
                            Text(
                                text = if (isSignedIn) "Synced" else "Not Synced",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isSignedIn) "Your data is backed up" else "Sign in to keep your data safe",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        if (isSignedIn) {
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sign Out", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = onSignIn,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sign In", fontSize = 12.sp)
                            }
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

            // ── Notifications ─────────────────────────────
            Text(
                "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (reminderSettings.isEmpty()) {
                NotificationSettingsLoading()
            } else {
                reminderSettings.forEach { setting ->

                    val title = when (setting.type) {
                        ReminderType.TASK -> "Task"
                        ReminderType.EXAM -> "Exam"
                        ReminderType.DAILY_HABIT -> "Daily Habit"
                        ReminderType.MISSED_TASK -> "Missed Task"
                        ReminderType.DAILY_GOAL -> "Daily Goal"
                        ReminderType.FOCUS_MODE -> "Focus Mode"
                    }

                    val subtitle = when (setting.type) {
                        ReminderType.TASK ->
                            "Get notified before task deadline"

                        ReminderType.EXAM ->
                            "Get alerts 1 and 3 days before exams"

                        ReminderType.DAILY_HABIT ->
                            "Daily reminder to keep up your study habit"

                        ReminderType.MISSED_TASK ->
                            "Alert if you have incomplete tasks at the end of the day"

                        ReminderType.DAILY_GOAL ->
                            "Alert if you haven't met your daily study goal"

                        ReminderType.FOCUS_MODE ->
                            "Pause all notifications during focus time"
                    }

                    NotificationSettingItem(
                        title = title,
                        subtitle = subtitle,
                        checked = setting.isEnabled,
                        onCheckedChange = {
                            onToggleReminder(
                                setting.copy(isEnabled = it)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/** Shown while Room emits the first real reminder settings list. */
@Composable
private fun NotificationSettingsLoading() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Loading notification settings...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
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
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        shape  = RoundedCornerShape(12.dp),
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
                colors = SwitchDefaults.colors(
                    checkedThumbColor  = Color.White,
                    checkedTrackColor  = Color.Black,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                    uncheckedBorderColor = Color(0xFF9E9E9E)
                )
            )
        }
    }
}
