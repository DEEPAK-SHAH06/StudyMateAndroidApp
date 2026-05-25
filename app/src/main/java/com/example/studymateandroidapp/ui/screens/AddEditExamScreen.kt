package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.ExamViewmodel

@Composable
fun AddEditExamScreen(
    examId: Long? = null,
    viewModel: ExamViewmodel,
    onNavigateBack: () -> Unit
) {
    // In a real app, if examId is not null, load the exam from ViewModel
    AddEditExamContent(
        isEdit = examId != null,
        onBack = onNavigateBack,
        onSave = { title, subject ->
            if (examId == null) {
                viewModel.addExam(Exam(title = title, subject = subject, examDate = System.currentTimeMillis()))
            } else {
                viewModel.updateExam(Exam(id = examId, title = title, subject = subject, examDate = System.currentTimeMillis()))
            }
            onNavigateBack()
        }
    )
}

@Composable
fun AddEditExamContent(
    isEdit: Boolean,
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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
                onClick = { if (title.isNotBlank() && subject.isNotBlank()) onSave(title, subject) },
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
        AddEditExamContent(isEdit = false, onBack = {}, onSave = { _, _ -> })
    }
}
