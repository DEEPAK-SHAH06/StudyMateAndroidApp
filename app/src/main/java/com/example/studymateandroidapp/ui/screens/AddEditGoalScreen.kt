package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.GoalStatus
import com.example.studymateandroidapp.viewmodel.GoalViewmodel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(
    goalId: Long? = null,
    viewModel: GoalViewmodel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    var status by remember { mutableStateOf(GoalStatus.NOT_STARTED) }
    var isLoading by remember { mutableStateOf(goalId != null) }

    LaunchedEffect(goalId) {
        if (goalId != null) {
            val goal = viewModel.getGoalById(goalId)
            if (goal != null) {
                title = goal.title
                description = goal.description
                progress = goal.currentValue.toFloat()
                status = goal.status
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (goalId == null) "New Goal" else "Edit Goal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val targetGoal = Goal(
                                id = goalId ?: 0,
                                title = title,
                                description = description,
                                currentValue = progress.toInt(),
                                targetValue = 100, // Default target for simplicity in this UI
                                status = status
                            )
                            if (goalId == null) {
                                viewModel.addGoal(targetGoal)
                            } else {
                                viewModel.updateGoal(targetGoal)
                            }
                            onNavigateBack()
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text(
                    text = "Progress: ${progress.toInt()}%",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = status.toDisplayString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        GoalStatus.entries.forEach { goalStatus ->
                            DropdownMenuItem(
                                text = { Text(goalStatus.toDisplayString()) },
                                onClick = {
                                    status = goalStatus
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        val targetGoal = Goal(
                            id = goalId ?: 0,
                            title = title,
                            description = description,
                            currentValue = progress.toInt(),
                            targetValue = 100,
                            status = status
                        )
                        if (goalId == null) {
                            viewModel.addGoal(targetGoal)
                        } else {
                            viewModel.updateGoal(targetGoal)
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank()
                ) {
                    Text("Save Goal")
                }
            }
        }
    }
}

private fun GoalStatus.toDisplayString(): String = when (this) {
    GoalStatus.NOT_STARTED -> "Pending"
    GoalStatus.IN_PROGRESS -> "In Progress"
    GoalStatus.COMPLETED -> "Completed"
    GoalStatus.ABANDONED -> "Abandoned"
}
