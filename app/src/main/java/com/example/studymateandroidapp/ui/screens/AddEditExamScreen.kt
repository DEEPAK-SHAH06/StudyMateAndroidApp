package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.relations.ExamWithDetails
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.ExamViewmodel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AddEditExamScreen(
    examId: Long? = null,
    viewModel: ExamViewmodel,
    onNavigateBack: () -> Unit
) {
    var initialTitle by remember { mutableStateOf("") }
    var initialSubject by remember { mutableStateOf("") }
    var initialDate by remember { mutableStateOf(LocalDate.now()) }
    var initialTime by remember { mutableStateOf(LocalTime.of(9, 0)) }

    if (examId != null) {
        val examState by viewModel.getExamWithDetails(examId).collectAsState(initial = null)
        LaunchedEffect(examState) {
            examState?.exam?.let {
                initialTitle = it.title
                initialSubject = it.subject
                val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.examDate), ZoneId.systemDefault())
                initialDate = dt.toLocalDate()
                initialTime = dt.toLocalTime()
            }
        }
    }

    AddEditExamContent(
        isEdit = examId != null,
        initialTitle = initialTitle,
        initialSubject = initialSubject,
        initialDate = initialDate,
        initialTime = initialTime,
        onBack = onNavigateBack,
        onSave = { title, subject, date, time ->
            val examDate = LocalDateTime.of(date, time)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            if (examId == null) {
                viewModel.addExam(Exam(title = title, subject = subject, examDate = examDate, isTimeSet = true))
            } else {
                viewModel.updateExam(Exam(id = examId, title = title, subject = subject, examDate = examDate, isTimeSet = true))
            }
            onNavigateBack()
        }
    )
}

@Composable
fun AddEditExamContent(
    isEdit: Boolean,
    initialTitle: String = "",
    initialSubject: String = "",
    initialDate: LocalDate = LocalDate.now(),
    initialTime: LocalTime = LocalTime.of(9, 0),
    onBack: () -> Unit,
    onSave: (String, String, LocalDate, LocalTime) -> Unit
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var subject by remember(initialSubject) { mutableStateOf(initialSubject) }
    var examDate by remember(initialDate) { mutableStateOf(initialDate) }
    var examTime by remember(initialTime) { mutableStateOf(initialTime) }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = if (isEdit) "Edit Exam" else "Add Exam",
                onBack = onBack
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

            Image(
                painter = painterResource(R.drawable.addexam),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Exam Title") },
                placeholder = { Text("e.g. Final Mathematics") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.title), contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                placeholder = { Text("e.g. Calculus II") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.subjects), contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = examDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = {},
                    label = { Text("Exam Date") },
                    readOnly = true,
                    leadingIcon = { 
                        IconButton(onClick = {
                            android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    examDate = LocalDate.of(year, month + 1, dayOfMonth)
                                },
                                examDate.year,
                                examDate.monthValue - 1,
                                examDate.dayOfMonth
                            ).show()
                        }) {
                            Icon(painter = painterResource(id = R.drawable.date), contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    },
                    modifier = Modifier.weight(1f).clickable {
                        android.app.DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                examDate = LocalDate.of(year, month + 1, dayOfMonth)
                            },
                            examDate.year,
                            examDate.monthValue - 1,
                            examDate.dayOfMonth
                        ).show()
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = examTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    onValueChange = {},
                    label = { Text("Time") },
                    readOnly = true,
                    leadingIcon = { 
                        IconButton(onClick = {
                            android.app.TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    examTime = LocalTime.of(hourOfDay, minute)
                                },
                                examTime.hour,
                                examTime.minute,
                                false
                            ).show()
                        }) {
                            Icon(painter = painterResource(id = R.drawable.time), contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    },
                    modifier = Modifier.weight(1f).clickable {
                        android.app.TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                examTime = LocalTime.of(hourOfDay, minute)
                            },
                            examTime.hour,
                            examTime.minute,
                            false
                        ).show()
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("e.g. Room 302") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.location), contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("Additional details...") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.note), contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { if (title.isNotBlank() && subject.isNotBlank()) onSave(title, subject, examDate, examTime) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Save Exam", fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddExamPreview() {
    MaterialTheme {
        AddEditExamContent(isEdit = false, onBack = {}, onSave = { _, _, _, _ -> })
    }
}
