package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
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
    val isSyncEnabled by viewModel.isSyncEnabled.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    
    // Explicitly collecting uiError
    val errorState by viewModel.uiError.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(errorState) {
        errorState?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val isSignedIn = currentUser != null
    val displayName = profileState.currentUsername.ifBlank {
        currentUser?.displayName ?: currentUser?.email ?: "Guest"
    }

    SettingsContent(
        displayName         = displayName,
        photoUrl            = profileState.photoUrl,
        isSignedIn          = isSignedIn,
        syncStatus          = syncStatus,
        isSyncEnabled       = isSyncEnabled,
        reminderSettings    = settings,
        snackbarHostState   = snackbarHostState,
        onBack              = onBack,
        onStatsClick        = onNavigateToStats,
        onAchievementsClick = onNavigateToAchievements,
        onEditProfileClick  = onNavigateToEditProfile,
        onToggleReminder    = { viewModel.toggleReminder(it) },
        onSignIn            = { 
            (context as? android.app.Activity)?.let { activity ->
                viewModel.signInWithGoogle(activity) 
            }
        },
        onSignOut           = { viewModel.signOut() },
        onToggleSync        = { viewModel.toggleSync(it) },
        onSyncNow           = { viewModel.triggerSync() }
    )
}

@Composable
fun SettingsContent(
    displayName: String,
    photoUrl: String?,
    isSignedIn: Boolean,
    syncStatus: String,
    isSyncEnabled: Boolean,
    reminderSettings: List<ReminderSetting>,
    snackbarHostState: SnackbarHostState,
    onBack: (() -> Unit)?,
    onStatsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onToggleReminder: (ReminderSetting) -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onToggleSync: (Boolean) -> Unit,
    onSyncNow: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = Color.White
                        )
                    }
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isSignedIn) "Cloud Sync" else "Not Signed In",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isSignedIn) "Sync Status: $syncStatus" else "Sign in to keep your data safe",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            if (isSignedIn) {
                                Switch(
                                    checked = isSyncEnabled,
                                    onCheckedChange = onToggleSync,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color.Black
                                    )
                                )
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

                        if (isSignedIn) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onSignOut,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Sign Out", fontSize = 12.sp)
                                }
                                if (isSyncEnabled) {
                                    Button(
                                        onClick = onSyncNow,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                        modifier = Modifier.weight(1f),
                                        enabled = syncStatus != "SYNCING"
                                    ) {
                                        Text("Sync Now", fontSize = 12.sp)
                                    }
                                }
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
                DefaultNotificationItems()
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

@Composable
private fun DefaultNotificationItems() {
    val defaults = listOf(
        "Task" to "Get notified before task deadline",
        "Exam" to "Get alerts 1 and 3 days before exams",
        "Daily habit" to "Daily reminder to keep up your study habit",
        "Missed task" to "Alert if you have incomplete tasks at the end of the day",
        "Daily goal" to "Alert if you haven't met your daily study goal",
        "Focus mode" to "Pause all notifications during focus time"
    )
    defaults.forEach { (title, subtitle) ->
        NotificationSettingItem(
            title    = title,
            subtitle = subtitle,
            checked  = true,
            onCheckedChange = {}
        )
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
                    checkedTrackColor  = Color.Black
                )
            )
        }
    }
}
