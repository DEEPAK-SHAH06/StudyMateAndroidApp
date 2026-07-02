package com.example.studymateandroidapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterInstrumentedTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSuccessfulRegistration() {
        // Navigate to Register screen from Login
        composeRule.onNodeWithTag("go_to_register")
            .performClick()

        // Enter Registration details
        composeRule.onNodeWithTag("email_field")
            .performTextInput("newuser@example.com")

        composeRule.onNodeWithTag("password_field")
            .performTextInput("password123")

        composeRule.onNodeWithTag("confirm_password_field")
            .performTextInput("password123")

        // Click Register
        composeRule.onNodeWithTag("register_button")
            .performClick()

        // Verify navigation or success state
        // composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
    }
}
