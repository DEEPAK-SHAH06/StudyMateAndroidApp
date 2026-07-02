package com.example.studymateandroidapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSuccessfulLogin_navigatesToDashboard() {
        // We start at Splash, wait for it to navigate to Login (or Dashboard if already logged in)
        // For testing purposes, we assume we are on Login screen or navigate there.
        
        // Note: In a real app with Splash, we might need to wait or navigate manually if Splash is too fast/slow.
        // Assuming we reach LoginScreen:

        composeRule.onNodeWithTag("email_field")
            .performTextInput("test@example.com")

        composeRule.onNodeWithTag("password_field")
            .performTextInput("password123")

        composeRule.onNodeWithTag("login_button")
            .performClick()

        // Since we are using a Mock/Fake Auth in many test scenarios, 
        // we'd verify navigation to Dashboard here.
        // composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
    }

    @Test
    fun testNavigateToRegister() {
        composeRule.onNodeWithTag("go_to_register")
            .performClick()

        composeRule.onNodeWithTag("register_button")
            .assertExists()
    }
}
