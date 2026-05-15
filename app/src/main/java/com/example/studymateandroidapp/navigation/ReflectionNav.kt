package com.example.studymateandroidapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studymateandroidapp.feature.reflection.ui.DailyReflectionBody
import com.example.studymateandroidapp.feature.reflection.ui.ReflectionHistoryBody

@Composable
fun ReflectionNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "today"
    ) {
        composable("today") {
            DailyReflectionBody(navController)
        }

        composable("history") {
            ReflectionHistoryBody(navController)
        }
    }
}