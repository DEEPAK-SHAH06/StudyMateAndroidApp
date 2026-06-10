package com.example.studymateandroidapp.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.data.model.Task
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
                title = if (taskId == null) "Add Task" else "Edit Task",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val context = androidx.compose.ui.platform.LocalContext.current
                
                DateTimeFieldSelector(
                    label = "Due Date",
                    value = dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onClick = {
                        android.app.DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                dueDate = LocalDate.of(year, month + 1, dayOfMonth)
                            },
                            dueDate.year,
                            dueDate.monthValue - 1,
                            dueDate.dayOfMonth
                        ).show()
                    },
                    leadingIcon = { Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.weight(1f)
                )

                DateTimeFieldSelector(
                    label = "Due Time",
                    value = dueTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    onClick = {
                        android.app.TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                dueTime = LocalTime.of(hourOfDay, minute)
                            },
                            dueTime.hour,
                            dueTime.minute,
                            false
                        ).show()
                    },
                    leadingIcon = { Icon(painter = painterResource(id = R.drawable.time), contentDescription = null, modifier = Modifier.size(20.dp)) },
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
        }
    }
}