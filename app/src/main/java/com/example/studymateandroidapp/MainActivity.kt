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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.example.studymateandroidapp.data.model.XpEvent
import com.example.studymateandroidapp.ui.ViewModelFactory
import com.example.studymateandroidapp.ui.components.XpOverlay
import com.example.studymateandroidapp.ui.navigation.AppNavHost
import com.example.studymateandroidapp.ui.navigation.BottomNavBar
import com.example.studymateandroidapp.ui.navigation.Screen
import com.example.studymateandroidapp.ui.theme.StudyMateAndroidAppTheme
import com.example.studymateandroidapp.utils.security.BiometricHelper
import com.example.studymateandroidapp.viewmodel.GamificationViewModel
import kotlinx.coroutines.delay

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
            var authChecked by remember { mutableStateOf(false) }

            // Gamification Overlay Logic
            val gamificationViewModel: GamificationViewModel = viewModel(factory = ViewModelFactory)
            val xpQueue = remember { mutableStateListOf<XpEvent>() }
            var currentXpEvent by remember { mutableStateOf<XpEvent?>(null) }
            var isXpVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                gamificationViewModel.xpEvents.collect { event ->
                    xpQueue.add(event)
                }
            }

            LaunchedEffect(xpQueue.size) {
                if (xpQueue.isNotEmpty() && !isXpVisible) {
                    currentXpEvent = xpQueue.removeAt(0)
                    isXpVisible = true
                    delay(2000) // Increased slightly for better readability + animation
                    isXpVisible = false
                    delay(500) // Transition out
                    currentXpEvent = null
                }
            }

            StudyMateAndroidAppTheme(themeMode = themeMode) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isAppLockEnabled && !isAuthenticated) {
                        // Show biometric prompt if not authenticated
                        if (!authChecked) {
                            BiometricHelper.promptBiometricAuth(
                                activity = this@MainActivity,
                                title = "Biometric Lock",
                                subtitle = "Scan to unlock StudyMate",
                                onSuccess = { 
                                    isAuthenticated = true
                                    authChecked = true
                                },
                                onError = { 
                                    // Handle failure
                                }
                            )
                            authChecked = true
                        }
                        
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

                    // Global XP Overlay
                    currentXpEvent?.let { event ->
                        XpOverlay(event = event, isVisible = isXpVisible)
                    }
                }
            }
        }
    }
}
