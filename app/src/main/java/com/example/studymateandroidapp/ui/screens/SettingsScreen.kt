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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.model.ReminderType
import com.example.studymateandroidapp.ui.components.ConfirmDeleteDialog
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.SettingViewmodel

@Composable
fun SettingsScreen(
    viewModel: SettingViewmodel,
    onBack: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isSyncEnabled by viewModel.isSyncEnabled.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    
    // Explicitly collecting uiError
    val errorState by viewModel.uiError.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
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
        themeMode           = themeMode,
        isAppLockEnabled    = isAppLockEnabled,
        reminderSettings    = settings,
        snackbarHostState   = snackbarHostState,
        onBack              = onBack,
        onStatsClick        = onNavigateToStats,
        onAchievementsClick = onNavigateToAchievements,
        onEditProfileClick  = onNavigateToEditProfile,
        onToggleReminder    = { viewModel.toggleReminder(it) },
        onThemeChange       = { viewModel.setThemeMode(it) },
        onToggleAppLock     = { viewModel.setAppLockEnabled(it) },
        onSignIn            = { 
            if (!isSignedIn) {
                onNavigateToLogin()
            }
        },
        onSignOut           = { viewModel.signOut() },
        onDeleteAccount     = { showDeleteConfirmation = true },
        onToggleSync        = { viewModel.toggleSync(it) },
        onSyncNow           = { viewModel.triggerSync() }
    )

    if (showDeleteConfirmation) {
        ConfirmDeleteDialog(
            itemName = "Account",
            onConfirm = {
                viewModel.deleteAccount()
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

@Composable
fun SettingsContent(
    displayName: String,
    photoUrl: String?,
    isSignedIn: Boolean,
    syncStatus: String,
    isSyncEnabled: Boolean,
    themeMode: Int,
    isAppLockEnabled: Boolean,
    reminderSettings: List<ReminderSetting>,
    snackbarHostState: SnackbarHostState,
    onBack: (() -> Unit)?,
    onStatsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onToggleReminder: (ReminderSetting) -> Unit,
    onThemeChange: (Int) -> Unit,
    onToggleAppLock: (Boolean) -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onToggleSync: (Boolean) -> Unit,
    onSyncNow: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Account?") },
            text = { Text("This will permanently delete your account and all your study data from the cloud. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAccount()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StudyMateTopBar(
                title = "",
                onBack = null,
                actions = {
                    IconButton(onClick =  onAchievementsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.achievements),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Achievements",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onStatsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.statistics),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Statistics",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
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
                    color = MaterialTheme.colorScheme.surfaceVariant
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Sync Card ─────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isSignedIn) "Cloud Sync" else "Not Signed In",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isSignedIn) "Sync Status: $syncStatus" else "Sign in to keep your data safe",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            if (isSignedIn) {
                                Switch(
                                    checked = isSyncEnabled,
                                    onCheckedChange = onToggleSync,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            } else {
                                Button(
                                    onClick = onSignIn,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Sign In", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }

                        if (isSignedIn) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onSignOut,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text("Sign Out", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                if (isSyncEnabled) {
                                    Button(
                                        onClick = onSyncNow,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.weight(1f),
                                        enabled = syncStatus != "SYNCING"
                                    ) {
                                        Text("Sync Now", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditProfileClick,
                    modifier = Modifier.height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Your Profile", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Appearance ─────────────────────────────
            Text(
                "Appearance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth()
                    .size(150.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(0.1.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = themeMode == 0, onClick = { onThemeChange(0) })
                        Text("System Default", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = themeMode == 1, onClick = { onThemeChange(1) })
                        Text("Light Mode", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = themeMode == 2, onClick = { onThemeChange(2) })
                        Text("Dark Mode", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Security ─────────────────────────────
            Text(
                "Security",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biometric Lock", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Require biometric scan to open app", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Switch(
                        checked = isAppLockEnabled,
                        onCheckedChange = onToggleAppLock,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Notifications ─────────────────────────────
            Text(
                "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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

            if (isSignedIn) {
                TextButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Account Permanently")
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
        "Daily goal" to "Alert if you haven't met your daily study goal"
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor  = MaterialTheme.colorScheme.primary,
                    checkedTrackColor  = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsContent(
            displayName = "Student",
            photoUrl = null,
            isSignedIn = false,
            syncStatus = "IDLE",
            isSyncEnabled = false,

            themeMode = 0,
            isAppLockEnabled = false,

            reminderSettings = listOf(
                ReminderSetting(ReminderType.TASK, true),
                ReminderSetting(ReminderType.EXAM, true),
                ReminderSetting(ReminderType.DAILY_HABIT, true),
                ReminderSetting(ReminderType.MISSED_TASK, true),
                ReminderSetting(ReminderType.DAILY_GOAL, true)
            ),

            snackbarHostState = SnackbarHostState(),

            onBack = {},
            onStatsClick = {},
            onAchievementsClick = {},
            onEditProfileClick = {},

            onToggleReminder = {},
            onThemeChange = {},
            onToggleAppLock = {},

            onSignIn = {},
            onSignOut = {},
            onDeleteAccount = {},
            onToggleSync = {},
            onSyncNow = {}
            )
            }
            }