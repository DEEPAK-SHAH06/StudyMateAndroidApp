package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.model.TaskStatus
import com.example.studymateandroidapp.ui.components.ConfirmDeleteDialog
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.TaskViewmodel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TaskScreen(
    viewModel: TaskViewmodel,
    onNavigateToAddTask: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    TaskContent(
        tasks = tasks,
        snackbarHostState = snackbarHostState,
        onAddTask = onNavigateToAddTask,
        onStatsClick        = onNavigateToStats,
        onAchievementsClick = onNavigateToAchievements,
        onTaskClick = onNavigateToEditTask,
        onToggleTask = { task ->
            val isCompleted = !task.isCompleted
            viewModel.updateTask(task.copy(
                isCompleted = isCompleted,
                status = if (isCompleted) TaskStatus.COMPLETED else TaskStatus.TODO,
                completedAt = if (isCompleted) LocalDate.now() else null
            ))
        },
        onDeleteTask = { taskToDelete = it },
        onTogglePin = { task -> viewModel.togglePinned(task.id) }
    )

    taskToDelete?.let { task ->
        ConfirmDeleteDialog(
            itemName = "Task",
            onConfirm = {
                viewModel.deleteTask(task) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Task deleted")
                    }
                }
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}

@Composable
fun TaskContent(
    tasks: List<Task>,
    snackbarHostState: SnackbarHostState,
    onAddTask: () -> Unit,
    onStatsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onTogglePin: (Task) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("Pending", "Completed", "Overdue")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    val filteredTasks = remember(tasks, searchQuery, selectedFilter) {
        tasks.filter { task ->
            val matchesQuery = task.title.contains(searchQuery, ignoreCase = true) ||
                    (task.subjectTag?.contains(searchQuery, ignoreCase = true) == true)

            val matchesFilter = when (selectedFilter) {
                "Pending" -> !task.isCompleted && !task.isOverdue
                "Completed" -> task.isCompleted
                "Overdue" -> task.isOverdue
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    val highPriorityTask = remember(tasks) {
        tasks.filter { !it.isCompleted && !it.isOverdue && it.priority == Priority.HIGH }
            .minByOrNull { it.dueDate ?: LocalDate.MAX }
    }

    val overdueTask = remember(tasks) {
        tasks.find { it.isOverdue }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StudyMateTopBar(
                title = "",
                actions = {
                    IconButton(onClick = onAchievementsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.achievements),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Achievements",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onStatsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.statistics),
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Statistics",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task",  modifier = Modifier.size(35.dp))

            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "My Tasks",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color =  MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp
            )
            Text(
                "Manage your study load with intentionality.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(6.dp))

            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

            Spacer(modifier = Modifier.height(16.dp))

            FilterChips(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: List
            val sectionTitle = when(selectedFilter) {
                "Pending" -> "Pending •"
                "Completed" -> "Completed •"
                "Overdue" -> "Overdue •"
                else -> "All Tasks •"
            }
            val sectionSubtitle = "${filteredTasks.size} TASKS"

            SectionHeader(title = sectionTitle, subtitle = sectionSubtitle)

            Spacer(modifier = Modifier.height(12.dp))

            filteredTasks.forEach { task ->
                TaskItemView(
                    task = task,
                    onToggle = { onToggleTask(task) },
                    onClick = { onTaskClick(task.id) },
                    onDelete = { onDeleteTask(task) },
                    onTogglePin = { onTogglePin(task) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredTasks.isEmpty() && overdueTask == null) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tasks found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (highPriorityTask != null) {
                PriorityTaskCard(highPriorityTask)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (overdueTask != null) {
                OverdueMilestoneCard(overdueTask)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth()
            .size(49.dp)
        ,
        placeholder = { Text("Search tasks, subjects...", fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun FilterChips(filters: List<String>, selectedFilter: String, onFilterSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(24.dp)
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = filter,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        if (subtitle.isNotEmpty()) {
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun TaskItemView(
    task: Task,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (task.isPinned) 2.dp else 1.dp,
            color = if (task.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Checkbox
            Surface(
                modifier = Modifier
                    .size(25.dp)
                    .padding(2.dp)
                    .clickable { onToggle() },
                shape = RoundedCornerShape(6.dp),

                border = BorderStroke(
                    2.dp,
                    if (task.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                ),
                color =
                    if (task.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface
            ) {
                if (task.isCompleted) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (task.isPinned) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(22.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "PINNED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(22.dp),
                        color = when (task.priority) {
                            Priority.HIGH -> Color(0xFFFFE5E5)
                            Priority.MEDIUM -> Color(0xFFFFF3CD)
                            Priority.LOW -> Color(0xFFE8F5E9)
                        }
                    ) {
                        Text(
                            text = task.priority.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = when (task.priority) {
                                Priority.HIGH -> Color(0xFFC62828)
                                Priority.MEDIUM -> Color(0xFFB26A00)
                                Priority.LOW -> Color(0xFF2E7D32)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (!task.subjectTag.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(22.dp),
                            color = Color(task.tagColor.toULong()),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = task.subjectTag.uppercase(),
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    if (task.dueTime != null) {
                        Icon(
                            painter = painterResource(R.drawable.time),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = task.dueTime.format(
                                java.time.format.DateTimeFormatter.ofPattern(
                                    "h:mm a"
                                )
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onTogglePin
            ) {
                Icon(
                    imageVector = if (task.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = if (task.isPinned) "Unpin" else "Pin",
                    modifier = Modifier.size(20.dp),
                    tint = if (task.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PriorityTaskCard(task: Task) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE9E9E9)
    ) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(
                        text = "HIGH PRIORITY",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description.ifBlank { "Focus on this critical task today." },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Image(
                painter = painterResource(id = R.drawable.review),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun OverdueMilestoneCard(task: Task) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF2F2F2)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "!", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.Red)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Overdue Milestone", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "${task.title} - Overdue!", style = MaterialTheme.typography.bodySmall)
            }
            Image(
                painter = painterResource(id = R.drawable.overdue),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    val mockTasks = listOf(
        Task(id = 1, title = "Cognitive Psychology Thesis", subjectTag = "PSYCHOLOGY", dueTime = LocalTime.of(13, 0)),
        Task(id = 2, title = "Advanced Calculus Problems", subjectTag = "MATHEMATICS", dueTime = LocalTime.of(14, 45))
    )
    MaterialTheme {
        TaskContent(
            tasks = mockTasks,
            snackbarHostState = SnackbarHostState(),
            onAddTask = {},
            onTaskClick = {},
            onStatsClick = {},
            onAchievementsClick = {},
            onToggleTask = {},
            onDeleteTask = {},
            onTogglePin = {}
        )
    }
}