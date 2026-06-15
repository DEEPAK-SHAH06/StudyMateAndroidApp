package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.ui.components.ConfirmDeleteDialog
import com.example.studymateandroidapp.ui.components.DateTimeFieldSelector
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.TaskViewmodel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AddEditTaskScreen(
    taskId: Long? = null,
    viewModel: TaskViewmodel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf(LocalDate.now()) }
    var dueTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var subjectTag by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(taskId) {
        if (taskId != null) {
            val task = viewModel.getTaskById(taskId)
            if (task != null) {
                title = task.title
                description = task.description
                priority = task.priority
                dueDate = task.dueDate ?: LocalDate.now()
                dueTime = task.dueTime ?: LocalTime.of(9, 0)
                subjectTag = task.subjectTag ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = if (taskId == null) "Create Task" else "Edit Task",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "NEW ENTRY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Organize Your Study\nStream",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 38.sp
                    )
                    Text(
                        text = "Capture the essence of your next milestone.\nClarity leads to focus.",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(R.drawable.create_task),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = subjectTag,
                onValueChange = { subjectTag = it },
                label = { Text("Subject / Tag") },
                placeholder = { Text("e.g. BIOLOGY") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Priority",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->

                    val iconRes = when (p) {
                        Priority.HIGH -> R.drawable.high
                        Priority.MEDIUM -> R.drawable.medium
                        Priority.LOW -> R.drawable.low
                    }

                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Icon(
                                    painter = painterResource(iconRes),
                                    contentDescription = p.name,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = p.name,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                DateTimeFieldSelector(
                    label = "Due Date",
                    value = dueDate.format(
                        DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    ),
                    onClick = {
                        android.app.DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dueDate = LocalDate.of(year, month + 1, day)
                            },
                            dueDate.year,
                            dueDate.monthValue - 1,
                            dueDate.dayOfMonth
                        ).show()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                DateTimeFieldSelector(
                    label = "Due Time",
                    value = dueTime.format(
                        DateTimeFormatter.ofPattern("hh:mm a")
                    ),
                    onClick = {
                        android.app.TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                dueTime = LocalTime.of(hour, minute)
                            },
                            dueTime.hour,
                            dueTime.minute,
                            false
                        ).show()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = Task(
                            id = taskId ?: 0,
                            title = title,
                            description = description,
                            priority = priority,
                            dueDate = dueDate,
                            dueTime = dueTime,
                            subjectTag = subjectTag.uppercase()
                        )
                        if (taskId == null) viewModel.addTask(task) else viewModel.updateTask(task)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Save Task", fontWeight = FontWeight.Bold)
            }

            if (taskId != null) {
                var showDeleteConfirm by remember { mutableStateOf(false) }

                TextButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Delete Task", fontWeight = FontWeight.Bold)
                }

                if (showDeleteConfirm) {
                    ConfirmDeleteDialog(
                        itemName = "Task",
                        onConfirm = {
                            val currentTask = Task(
                                id = taskId,
                                title = title,
                                description = description,
                                priority = priority,
                                dueDate = dueDate,
                                dueTime = dueTime,
                                subjectTag = subjectTag.uppercase()
                            )
                            viewModel.deleteTask(currentTask) {
                                onNavigateBack()
                            }
                            showDeleteConfirm = false
                        },
                        onDismiss = { showDeleteConfirm = false }
                    )
                }
            }
        }
    }
}
