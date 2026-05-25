package com.example.studymateandroidapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studymateandroidapp.R

@Composable
fun DailyReflectionScreen(
    navController: NavController
) {
    var reflection by remember { mutableStateOf("") }
    var highlight by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(R.drawable.back_arrow),
                        contentDescription = "back"
                    )
                }

                Text(
                    "Daily Reflection",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Today",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    HorizontalDivider(
                        modifier = Modifier.width(120.dp)
                            .padding(top = 8.dp),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            navController.navigate("history")
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.width(120.dp)
                            .padding(top = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 25.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Friday",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text("May 8, 2026",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.reflection),
                            contentDescription = "reflection",
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("How are you feeling?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("😊",
                        "😴",
                        "🙂",
                        "💪",
                        "🤯",
                        "😭"
                    ).forEach { mood ->
                        MoodEmoji(
                            emoji = mood,
                            isSelected = selectedMood == mood,
                            onClick = {
                                selectedMood = mood
                            })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("What did you study today?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reflection,
                    onValueChange = {
                        reflection = it
                    },
                    modifier = Modifier.fillMaxWidth()
                        .height(150.dp),
                    placeholder = {
                        Text("Write freely... what went well, what you learned, any questions?")
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Highlight
                Text("⭐ Study highlight of the day",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = highlight,
                    onValueChange = {
                        highlight = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("e.g. Finally understood quadratic equations!")
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {

                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.save),
                            contentDescription = "save"
                        )

                        Text("Save Reflection",
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoodEmoji(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(45.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(
                1.dp,
                if (isSelected) Color.Black else Color.LightGray,
                RoundedCornerShape(50.dp)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(emoji,
            fontSize = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyReflectionPreview() {
    DailyReflectionScreen(
        navController = rememberNavController()
    )
}