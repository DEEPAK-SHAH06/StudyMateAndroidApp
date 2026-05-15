package com.example.studymateandroidapp.feature.reflection.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.studymateandroidapp.navigation.ReflectionNav

class ReflectionHistoryUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReflectionNav()
        }
    }
}

data class ReflectionData(
    val day: String,
    val date: String,
    val mood: String,
    val subject: String,
    val highlight: String
)

@Composable
fun ReflectionHistoryBody(
    navController: NavController
) {
    val reflectionList = listOf(
        ReflectionData(
            day = "Thursday",
            date = "April 30, 2026",
            mood = "💪",
            subject = "Kotlin",
            highlight = "Finished Kotlin UI screens and finally understood Compose layouts."
        ),

        ReflectionData(
            day = "Wednesday",
            date = "April 29, 2026",
            mood = "😴",
            subject = "Jetpack Compose",
            highlight = "Struggled with statistics screen but managed to complete most of it."
        ),

        ReflectionData(
            day = "Tuesday",
            date = "April 28, 2026",
            mood = "😊",
            subject = "High Fidelity",
            highlight = "Completed Figma high fidelity design with the group."
        )
    )

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.back_arrow),
                            contentDescription = "back"
                        )
                    }

                    Text("Daily Reflection",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Today",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable {
                                navController.navigate("today")
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.width(120.dp)
                                .padding(top = 8.dp),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        HorizontalDivider(
                            modifier = Modifier.width(120.dp)
                                .padding(top = 8.dp),
                            thickness = 2.dp,
                            color = Color.Black
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(reflectionList.size) { index ->
                val reflection = reflectionList[index]

                Box(
                    modifier = Modifier.padding(horizontal = 25.dp)
                ) {
                    ReflectionHistoryCard(
                        day = reflection.day,
                        date = reflection.date,
                        mood = reflection.mood,
                        subject = reflection.subject,
                        highlight = reflection.highlight
                    )
                }
            }
        }
    }
}


@Composable
fun ReflectionHistoryCard(
    day : String,
    date: String,
    mood: String,
    subject : String,
    highlight : String
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    Text(day,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Text(date,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.Gray
                    )
                }

                Text(mood,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(subject,
                fontSize = 15.sp)

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier.border(
                        1.dp,
                        Color.LightGray.copy(0.5f),
                        RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(0.2f))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐")

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(highlight,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReflectionHistoryPreview() {
    ReflectionHistoryBody(
        navController = rememberNavController()
    )
}