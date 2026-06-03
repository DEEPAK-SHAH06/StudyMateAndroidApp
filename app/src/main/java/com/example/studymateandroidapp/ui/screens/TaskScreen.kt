package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.model.TaskStatus
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.TaskViewmodel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskScreen(
    viewModel: TaskViewmodel,
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState()
    
    TaskContent(
        tasks = tasks,
        onAddTask = onNavigateToAddTask,
        onTaskClick = onNavigateToEditTask,
        onToggleTask = { task ->
            viewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    )
}

@Composable
fun TaskContent(
    tasks: List<Task>,
    onAddTask: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onToggleTask: (Task) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("Pending", "Completed", "Overdue")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    val filteredTasks = remember(tasks, searchQuery, selectedFilter) {
        tasks.filter { task ->
            val matchesQuery = task.title.contains(searchQuery, ignoreCase = true) ||
                    (task.subjectTag?.contains(searchQuery, ignoreCase = true) == true)
            
            val matchesFilter = when (selectedFilter) {
                "Pending" -> !task.isCompleted
                "Completed" -> task.isCompleted
                "Overdue" -> !task.isCompleted && task.dueDate != null && task.dueDate!!.isBefore(LocalDate.now())
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    val highPriorityTask = remember(tasks) {
        tasks.filter { !it.isCompleted && it.priority == Priority.HIGH }
            .minByOrNull { it.dueDate ?: LocalDate.MAX }
    }

    val overdueTask = remember(tasks) {
        tasks.find { !it.isCompleted && it.dueDate != null && it.dueDate!!.isBefore(LocalDate.now()) }
    }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "My Tasks",
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(painter = painterResource(id = R.drawable.statistics), contentDescription = "Stats")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Manage your study load intentionally.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    onClick = { onTaskClick(task.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredTasks.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tasks found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
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
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search tasks, subjects...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF2F2F2),
            unfocusedContainerColor = Color(0xFFF2F2F2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun FilterChips(filters: List<String>, selectedFilter: String, onFilterSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) Color.Black else Color(0xFFF2F2F2)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else Color.Black,
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
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (subtitle.isNotEmpty()) {
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun TaskItemView(task: Task, onToggle: () -> Unit, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F8F8),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color.Black)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    task.subjectTag?.let { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.dueTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "No time",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Icon(Icons.Default.MoreVert, contentDescription = null)
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
            onAddTask = {},
            onTaskClick = {},
            onToggleTask = {}
        )
    }
}
