package com.example.studymateandroidapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.studymateandroidapp.MainActivity
import org.junit.Rule
import org.junit.Test

class TaskInstruementedTesting {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAddTaskFlow() {
        // Assume we are already logged in or the app starts at the Dashboard/TaskScreen
        // In a real scenario, you might need to handle login first if not session-persistent
        
        // 1. Click on FAB to add task
        composeTestRule.onNodeWithTag("add_task_fab").performClick()

        // 2. Fill in task details
        composeTestRule.onNodeWithTag("task_title_field").performTextInput("Finish Homework")
        composeTestRule.onNodeWithTag("task_subject_field").performTextInput("MATH")
        composeTestRule.onNodeWithTag("task_description_field").performTextInput("Complete all exercises in Chapter 5")

        // 3. Select priority
        composeTestRule.onNodeWithTag("priority_high").performClick()

        // 4. Save the task
        composeTestRule.onNodeWithTag("save_task_button").performClick()

        // 5. Verify the task appears in the list
        composeTestRule.onNodeWithTag("task_item_Finish Homework").assertIsDisplayed()
        composeTestRule.onNodeWithText("MATH").assertIsDisplayed()
        composeTestRule.onNodeWithText("HIGH").assertIsDisplayed()
    }

    @Test
    fun testEditTaskFlow() {
        // 1. Click on an existing task (assuming "Finish Homework" exists from previous test or setup)
        // For instrumented tests, it's better if they are independent or we create the data first
        
        // Let's create one first to be sure
        composeTestRule.onNodeWithTag("add_task_fab").performClick()
        composeTestRule.onNodeWithTag("task_title_field").performTextInput("Initial Task")
        composeTestRule.onNodeWithTag("save_task_button").performClick()
        
        // Now edit it
        composeTestRule.onNodeWithTag("task_item_Initial Task").performClick()
        
        composeTestRule.onNodeWithTag("task_title_field").performTextReplacement("Updated Task")
        composeTestRule.onNodeWithTag("priority_low").performClick()
        composeTestRule.onNodeWithTag("save_task_button").performClick()
        
        // Verify update
        composeTestRule.onNodeWithTag("task_item_Updated Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("LOW").assertIsDisplayed()
    }

    @Test
    fun testDeleteTaskFlow() {
        // 1. Create a task to delete
        composeTestRule.onNodeWithTag("add_task_fab").performClick()
        composeTestRule.onNodeWithTag("task_title_field").performTextInput("Task to Delete")
        composeTestRule.onNodeWithTag("save_task_button").performClick()
        
        // 2. Open it
        composeTestRule.onNodeWithTag("task_item_Task to Delete").performClick()
        
        // 3. Click delete button
        composeTestRule.onNodeWithTag("delete_task_button").performClick()
        
        // 4. Confirm in dialog
        composeTestRule.onNodeWithTag("confirm_delete_button").performClick()
        
        // 5. Verify it's gone
        composeTestRule.onNodeWithTag("task_item_Task to Delete").assertDoesNotExist()
    }
}
