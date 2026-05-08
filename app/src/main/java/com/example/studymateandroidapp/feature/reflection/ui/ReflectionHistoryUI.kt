package com.example.studymateandroidapp.feature.reflection.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R

class ReflectionHistoryUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReflectionHistoryBody()
        }
    }
}

@Composable
fun ReflectionHistoryBody() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painterResource(R.drawable.back_arrow),
                    "back"
                )
            }

            Text("Daily Reflection",
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
                    color = Color.Gray
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

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ReflectionHistoryCard(
                date = "April 30, 2026",
                mood = "💪",
                reflection = "Finished Kotlin UI screens and finally understood Compose layouts."
            )

            ReflectionHistoryCard(
                date = "April 29, 2026",
                mood = "😴",
                reflection = "Struggled with statistics screen but managed to complete most of it."
            )

            ReflectionHistoryCard(
                date = "April 28, 2026",
                mood = "😊",
                reflection = "Completed Figma high fidelity design with the group."
            )
        }
    }
}

@Composable
fun ReflectionHistoryCard(
    date: String,
    mood: String,
    reflection: String
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
                Text(date,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(mood,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(reflection,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReflectionHistoryPreview() {
    ReflectionHistoryBody()
}