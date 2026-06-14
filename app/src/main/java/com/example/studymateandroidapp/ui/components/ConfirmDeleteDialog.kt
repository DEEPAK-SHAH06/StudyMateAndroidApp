package com.example.studymateandroidapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * A unified delete confirmation dialog used across the application.
 *
 * @param itemName The name of the item to be deleted (e.g., "Task", "Exam").
 * @param onConfirm Callback executed when the user confirms deletion.
 * @param onDismiss Callback executed when the user cancels or dismisses the dialog.
 */
@Composable
fun ConfirmDeleteDialog(
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete $itemName?")
        },
        text = {
            Text(text = "This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )
}
