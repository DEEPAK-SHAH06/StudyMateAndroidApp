package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymateandroidapp.data.model.GoalSubtask
import com.example.studymateandroidapp.viewmodel.GoalViewmodel

@Composable
fun AddEditGoalScreen(
    viewModel: GoalViewmodel,
    goalId: Long? = null,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(goalId) {
        if (goalId != null) {
            viewModel.loadGoalForEdit(goalId)
        } else {
            viewModel.resetForm()
        }
    }

    // Handle single-event navigation back
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    AddGoalContent(
        uiState = uiState,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onAddSubtask = viewModel::onAddSubtask,
        onToggleSubtask = viewModel::onToggleSubtask,
        onRemoveSubtask = viewModel::onRemoveSubtask,
        onSaveGoal = viewModel::onSaveGoal,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalContent(
    uiState: GoalViewmodel.GoalFormUiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAddSubtask: (String) -> Unit,
    onToggleSubtask: (Int) -> Unit,
    onRemoveSubtask: (Int) -> Unit,
    onSaveGoal: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var newSubtaskTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background),
                title = { Text(if (uiState.isEditMode) "Edit Goal" else "Add Goal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                        windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp, vertical = 5.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                label = { Text("Goal Title") },
                placeholder = { Text("e.g. Complete 50 Calculus problems") },
                leadingIcon = { Icon(Icons.Default.Title, null) },
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Description (Optional)") },
                placeholder = { Text("More details about your goal...") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("Goal Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Breaking your goal into smaller steps automatically updates your progress.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Add Subtask Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSubtaskTitle,
                    onValueChange = { newSubtaskTitle = it },
                    label = { Text("Add step...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            onAddSubtask(newSubtaskTitle)
                            newSubtaskTitle = ""
                        }
                    )
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        onAddSubtask(newSubtaskTitle)
                        newSubtaskTitle = ""
                    },
                    enabled = newSubtaskTitle.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Step")
                }
            }

            // Subtask List
            uiState.subtasks.forEachIndexed { index, subtask ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = subtask.isCompleted,
                        onCheckedChange = { onToggleSubtask(index) }
                    )
                    Text(
                        text = subtask.title,
                        modifier = Modifier.weight(1f),
                        style = if (subtask.isCompleted)
                            MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                        else
                            MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { onRemoveSubtask(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (uiState.subtasks.isNotEmpty()) {
                val progress = if (uiState.targetValue > 0) uiState.currentValue.toFloat() / uiState.targetValue else 0f
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Current Progress: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    Text("${uiState.currentValue} of ${uiState.targetValue} steps completed", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onSaveGoal,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Goal",color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddGoalPreview() {
    MaterialTheme {
        AddGoalContent(
            uiState = GoalViewmodel.GoalFormUiState(
                title = "Learn Quantum Physics",
                description = "Understand the basics of quantum mechanics.",
                subtasks = listOf(
                    GoalSubtask(
                        "Read Introduction",
                        true
                    ),
                    GoalSubtask("Solve basic problems", false)
                ),
                targetValue = 2,
                currentValue = 1
            ),
            onTitleChanged = {},
            onDescriptionChanged = {},
            onAddSubtask = {},
            onToggleSubtask = {},
            onRemoveSubtask = {},
            onSaveGoal = {},
            onNavigateBack = {}
        )
    }
}
