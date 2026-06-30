package com.example.studymateandroidapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.ui.ViewModelFactory
import com.example.studymateandroidapp.ui.navigation.AppNavHost
import com.example.studymateandroidapp.ui.navigation.BottomNavBar
import com.example.studymateandroidapp.ui.navigation.Screen
import com.example.studymateandroidapp.ui.theme.StudyMateAndroidAppTheme
import com.example.studymateandroidapp.utils.security.BiometricHelper
import com.example.studymateandroidapp.viewmodel.GamificationViewModel

import androidx.compose.runtime.mutableStateListOf
import com.example.studymateandroidapp.ui.components.CelebrationOverlay
import com.example.studymateandroidapp.data.model.CelebrationEvent

class MainActivity : FragmentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkNotificationPermission()
        
        val preferenceManager = PreferenceManager(this)

        setContent {
            val themeMode by preferenceManager.themeMode.collectAsState(initial = 0)
            val isAppLockEnabled by preferenceManager.isAppLockEnabled.collectAsState(initial = false)
            
            var isAuthenticated by remember { mutableStateOf(false) }

            LaunchedEffect(isAppLockEnabled, isAuthenticated) {
                if (isAppLockEnabled && !isAuthenticated) {
                    BiometricHelper.promptBiometricAuth(
                        activity = this@MainActivity,
                        title = "Biometric Lock",
                        subtitle = "Scan to unlock StudyMate",
                        onSuccess = { 
                            isAuthenticated = true
                        },
                        onError = { 
                            // Handle failure
                        }
                    )
                }
            }

            // Gamification Logic (Queue-based Overlay)
            val gamificationViewModel: GamificationViewModel = viewModel(factory = ViewModelFactory)
            val celebrationQueue = remember { mutableStateListOf<CelebrationEvent>() }
            var currentCelebration by remember { mutableStateOf<CelebrationEvent?>(null) }
            var isCelebrationVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                gamificationViewModel.celebrationEvents.collect { event ->
                    celebrationQueue.add(event)
                }
            }

            LaunchedEffect(celebrationQueue.size, isCelebrationVisible) {
                if (celebrationQueue.isNotEmpty() && !isCelebrationVisible) {
                    currentCelebration = celebrationQueue.removeAt(0)
                    isCelebrationVisible = true
                }
            }

            StudyMateAndroidAppTheme(themeMode = themeMode) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isAppLockEnabled && !isAuthenticated) {
                        // While authenticating, show a blank screen
                        Box(modifier = Modifier.fillMaxSize())
                    } else {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        val showBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                if (showBottomBar) {
                                    BottomNavBar(navController = navController)
                                }
                            }
                        ) { innerPadding ->
                            AppNavHost(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    // Global Celebration Overlay
                    currentCelebration?.let { event ->
                        CelebrationOverlay(
                            event = event,
                            isVisible = isCelebrationVisible,
                            onDismiss = {
                                isCelebrationVisible = false
                                currentCelebration = null
                            }
                        )
                    }
                }
            }
        }
    }
}
