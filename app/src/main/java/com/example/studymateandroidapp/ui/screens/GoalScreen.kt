package com.example.studymateandroidapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymateandroidapp.R
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
    onNavigateBack: () -> Unit,
) {
    val listState by viewModel.listState.collectAsStateWithLifecycle()
    var goalIdToDelete by remember { mutableStateOf<Long?>(null) }

    GoalScreenContent(
        listState = listState,
        onNavigateToAddGoal = onNavigateToAddGoal,
        onNavigateToEditGoal = onNavigateToEditGoal,
        onNavigateBack = onNavigateBack,
        onDeleteGoal = { goalIdToDelete = it },
        onFilterChanged = viewModel::onFilterChanged
    )

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
fun GoalScreenContent(
    listState: GoalViewmodel.GoalListUiState,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToEditGoal: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onFilterChanged: (GoalViewmodel.GoalFilter) -> Unit
) {
    val selectedFilter = if (listState.filter == GoalViewmodel.GoalFilter.COMPLETED) "Finished" else "Working"
    val filteredGoals = listState.goals

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "My Goals",
                onBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddGoal,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal", modifier = Modifier.size(35.dp))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (listState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Featured Card - ALWAYS VISIBLE
                    item {
                        FeaturedGoalCard(
                            featuredGoal = filteredGoals.firstOrNull()?.goal,
                            selectedFilter = selectedFilter,
                            onFilterSelected = {
                                val newFilter = if (it == "Finished") GoalViewmodel.GoalFilter.COMPLETED else GoalViewmodel.GoalFilter.ACTIVE
                                onFilterChanged(newFilter)
                            },
                            onEditGoal = { onNavigateToEditGoal(it.id) }
                        )
                    }

                    // 2. Section Header
                    item {
                        Text(
                            text = if (selectedFilter == "Working") "Active Pursuits" else "Achievements",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // 3. Content: Either the List OR the Empty State
                    if (filteredGoals.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyGoalsState()
                            }
                        }
                    } else {
                        items(filteredGoals, key = { it.goal.id }) { item ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                GoalCard(
                                    goal = item.goal,
                                    progressPercent = item.progressPercent,
                                    onClick = { onNavigateToEditGoal(item.goal.id) },
                                    onDelete = { onDeleteGoal(item.goal.id) }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun EmptyGoalsState() {
    Box(
        Modifier.fillMaxSize(),
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
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = onClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$progressPercent%",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${goal.currentValue} / ${goal.targetValue} units completed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FeaturedGoalCard(
    featuredGoal: Goal?,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onEditGoal: (Goal) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    Text(
                        text = "Work hard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color =  MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text =  "to achieve your goals",
                        style = MaterialTheme.typography.bodySmall,
                        color =  MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val imageRes = if (isDark) {
                    R.drawable.work_dark
                } else {
                    R.drawable.work1
                }

                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(140.dp)
                )
            }

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {
                FilterTabButton(
                    text = "Working",
                    isSelected = selectedFilter == "Working",
                    onClick = { onFilterSelected("Working") },
                    modifier = Modifier.weight(1f)
                )
                FilterTabButton(
                    text = "Finished",
                    isSelected = selectedFilter == "Finished",
                    onClick = { onFilterSelected("Finished") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FilterTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        label = "bg"
    )
    val textColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "text"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GoalScreenPreview() {
    MaterialTheme {
        GoalScreenContent(
            listState = GoalViewmodel.GoalListUiState(
                goals = listOf(
                    GoalViewmodel.GoalDisplayItem(
                        goal = Goal(
                            id = 1,
                            title = "Learn Jetpack Compose",
                            description = "Master building modern Android UIs",
                            status = GoalStatus.IN_PROGRESS,
                            currentValue = 65,
                            targetValue = 100
                        ),
                        progressPercent = 65
                    ),
                    GoalViewmodel.GoalDisplayItem(
                        goal = Goal(
                            id = 2,
                            title = "Finish Room Database",
                            description = "Implement local storage",
                            status = GoalStatus.COMPLETED,
                            currentValue = 10,
                            targetValue = 10
                        ),
                        progressPercent = 100
                    )
                ),
                isLoading = false
            ),
            onNavigateToAddGoal = {},
            onNavigateToEditGoal = {},
            onNavigateBack = {},
            onDeleteGoal = {},
            onFilterChanged = {}
        )
    }
}
