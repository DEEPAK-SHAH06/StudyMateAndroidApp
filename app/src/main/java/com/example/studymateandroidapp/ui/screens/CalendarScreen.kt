package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.CalendarViewModel
import com.example.studymateandroidapp.viewmodel.CalendarViewModel.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToTask: (Long) -> Unit,
    onNavigateToExam: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onAddTask: (LocalDate) -> Unit,
    onAddExam: (LocalDate) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CalendarContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onGoToToday = viewModel::onGoToToday,
        onMonthChange = viewModel::onMonthChanged,
        onDateSelected = viewModel::onDateSelected,
        onNavigateToTask = onNavigateToTask,
        onNavigateToExam = onNavigateToExam,
        onAddTask = onAddTask,
        onAddExam = onAddExam
    )
}

@Composable
fun CalendarContent(
    uiState: CalendarUiState,
    onNavigateBack: () -> Unit,
    onGoToToday: () -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onNavigateToTask: (Long) -> Unit,
    onNavigateToExam: (Long) -> Unit,
    onAddTask: (LocalDate) -> Unit,
    onAddExam: (LocalDate) -> Unit
) {
    var showActionDialog by remember { mutableStateOf(false) }
    var selectedDateForAction by remember { mutableStateOf<LocalDate?>(null) }

    if (showActionDialog && selectedDateForAction != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Actions for ${selectedDateForAction}") },
            text = { Text("What would you like to add?") },
            confirmButton = {
                TextButton(onClick = {
                    showActionDialog = false
                    onAddTask(selectedDateForAction!!)
                }) { Text("Add Task") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showActionDialog = false
                    onAddExam(selectedDateForAction!!)
                }) { Text("Add Exam") }
            }
        )
    }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Study Calendar",
                onBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onGoToToday) {
                        Icon(Icons.Default.Today, "Today")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                selectedDateForAction = uiState.selectedDate
                showActionDialog = true 
            }) {
                Icon(Icons.Default.Add, "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MonthSelector(
                currentMonth = uiState.currentMonth,
                onMonthChange = onMonthChange
            )

            CalendarGrid(
                currentMonth = uiState.currentMonth,
                selectedDate = uiState.selectedDate,
                eventsByDate = uiState.eventsByDate,
                onDateSelected = { date ->
                    onDateSelected(date)
                    selectedDateForAction = date
                    showActionDialog = true
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            AgendaSection(
                selectedDate = uiState.selectedDate,
                events = uiState.eventsByDate[uiState.selectedDate] ?: emptyList(),
                onNavigateToTask = onNavigateToTask,
                onNavigateToExam = onNavigateToExam
            )
        }
    }
}

@Composable
private fun MonthSelector(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(
            text = "$monthName ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ChevronLeft, "Previous Month")
            }
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ChevronRight, "Next Month")
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value
    val offset = firstDayOfMonth - 1

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val totalCells = 42
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp),
            userScrollEnabled = false
        ) {
            items(totalCells) { index ->
                val dayNumber = index - offset + 1
                if (dayNumber in 1..daysInMonth) {
                    val date = currentMonth.atDay(dayNumber)
                    CalendarDayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == LocalDate.now(),
                        events = eventsByDate[date] ?: emptyList(),
                        onClick = { onDateSelected(date) }
                    )
                } else {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    events: List<CalendarEvent>,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isToday -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )

        Row(
            modifier = Modifier.height(6.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val hasTasks = events.any { it is CalendarEvent.TaskEvent }
            val hasExams = events.any { it is CalendarEvent.ExamEvent }

            if (hasTasks) {
                Box(Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            }
            if (hasExams) {
                Box(Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error))
            }
        }
    }
}

@Composable
private fun AgendaSection(
    selectedDate: LocalDate,
    events: List<CalendarEvent>,
    onNavigateToTask: (Long) -> Unit,
    onNavigateToExam: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(
            text = "Agenda for ${selectedDate.dayOfMonth} $monthName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (events.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No sessions or exams scheduled.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(events) { event ->
                    when (event) {
                        is CalendarEvent.TaskEvent -> {
                            AgendaItem(
                                title = event.task.title,
                                subtitle = "Task • Due at ${event.task.dueDate}",
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { onNavigateToTask(event.task.id) }
                            )
                        }
                        is CalendarEvent.ExamEvent -> {
                            AgendaItem(
                                title = event.exam.title,
                                subtitle = "Exam • ${event.exam.subject}",
                                color = MaterialTheme.colorScheme.error,
                                onClick = { onNavigateToExam(event.exam.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaItem(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(4.dp, 32.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    MaterialTheme {
        CalendarContent(
            uiState = CalendarUiState(
                currentMonth = YearMonth.now(),
                selectedDate = LocalDate.now(),
                eventsByDate = emptyMap()
            ),
            onNavigateBack = {},
            onGoToToday = {},
            onMonthChange = {},
            onDateSelected = {},
            onNavigateToTask = {},
            onNavigateToExam = {},
            onAddTask = {},
            onAddExam = {}
        )
    }
}
