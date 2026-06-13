package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.GoalStatus
import com.example.studymateandroidapp.ui.components.ConfirmDeleteDialog
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.GoalViewmodel

@Composable
fun GoalScreen(
    viewModel: GoalViewmodel,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToEditGoal: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()
    var goalIdToDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Study Goals",
                onBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddGoal,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        if (uiState.goals.isEmpty() && !uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No goals set yet. Start big!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.goals, key = { it.goal.id }) { item ->
                    GoalCard(
                        goal = item.goal,
                        progressPercent = item.progressPercent,
                        onClick = { onNavigateToEditGoal(item.goal.id) },
                        onDelete = { goalIdToDelete = item.goal.id }
                    )
                }
            }
        }
    }

    goalIdToDelete?.let { goalId ->
        ConfirmDeleteDialog(
            itemName = "Goal",
            onConfirm = {
                viewModel.onDeleteGoal(goalId)
                goalIdToDelete = null
            },
            onDismiss = { goalIdToDelete = null }
        )
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    progressPercent: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = progressPercent / 100f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (goal.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = goal.status)
                
                Text(
                    text = "${goal.currentValue} / ${goal.targetValue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: GoalStatus) {
    val containerColor = when (status) {
        GoalStatus.COMPLETED -> Color(0xFFE8F5E9)
        GoalStatus.IN_PROGRESS -> Color(0xFFE3F2FD)
        GoalStatus.NOT_STARTED -> Color(0xFFF5F5F5)
        GoalStatus.ABANDONED -> Color(0xFFFFEBEE)
    }
    
    val contentColor = when (status) {
        GoalStatus.COMPLETED -> Color(0xFF2E7D32)
        GoalStatus.IN_PROGRESS -> Color(0xFF1976D2)
        GoalStatus.NOT_STARTED -> Color(0xFF616161)
        GoalStatus.ABANDONED -> Color(0xFFC62828)
    }

    val label = when (status) {
        GoalStatus.COMPLETED -> "Completed"
        GoalStatus.IN_PROGRESS -> "In Progress"
        GoalStatus.NOT_STARTED -> "Pending"
        GoalStatus.ABANDONED -> "Abandoned"
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GoalScreenPreview() {
    MaterialTheme {
        GoalCard(
            goal = Goal(
                id = 1,
                title = "Learn Jetpack Compose",
                description = "Master building modern Android UIs with Compose Material 3",
                status = GoalStatus.IN_PROGRESS,
                currentValue = 65,
                targetValue = 100
            ),
            progressPercent = 65,
            onClick = {},
            onDelete = {}
        )
    }
}
