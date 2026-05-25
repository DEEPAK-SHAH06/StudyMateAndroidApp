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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExamScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Exam",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 30.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(top = 30.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back_arrow),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(

                painter = painterResource(R.drawable.addexam),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp)
                    .padding(20.dp)

            )
            OutlinedTextField(
                value = "",
                onValueChange = {  },
                label = { Text("Exam Title") },
                placeholder = { Text("e.g. Final Mathematics") },
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.title), contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Subject") },
                placeholder = { Text("e.g. Calculus II") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.subjects), contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Select Exam Date") },
                placeholder = { Text("e.g. Calculus II") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.new_entry), contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )



            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Location") },
                placeholder = { Text("e.g. Room 302") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.location), contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Notes") },
                placeholder = { Text("Additional details...") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.note), contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {  },
                modifier = Modifier.width(120.dp).align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Save Exam")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExamPreview() {
        AddEditExamScreen()
}