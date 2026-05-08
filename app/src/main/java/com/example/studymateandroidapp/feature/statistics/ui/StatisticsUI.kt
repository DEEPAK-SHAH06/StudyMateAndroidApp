package com.example.studymateandroidapp.feature.statistics.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R

class StatisticsUI : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StatisticsBody()
        }
    }
}

@Composable
fun StatisticsBody() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
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

            Text("Statistics",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 33.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(
                    painterResource(R.drawable.task_done),
                    "4/8",
                    "Task Done",
                    "50%"
                )

                StatCard(
                    painterResource(R.drawable.total),
                    "0h 0m",
                    "Total Study",
                    ""
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(
                    painterResource(R.drawable.task_done),
                    "0",
                    "Goals Met",
                    ""
                )

                StatCard(
                    painterResource(R.drawable.total),
                    "0h 0m",
                    "This Week",
                    "Today: 0m"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                "Weekly Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    WeeklyBar(0,"Sun")
                    WeeklyBar(0,"Mon")
                    WeeklyBar(40,"Tue")
                    WeeklyBar(0,"Wed")
                    WeeklyBar(80,"Thu")
                    WeeklyBar(0,"Fri")
                    WeeklyBar(0,"Sat")
                }
            }
        }
    }
}

@Composable
fun StatCard(
    painter : Painter,
    overview : String,
    description : String,
    subdescription : String
) {
    OutlinedCard(modifier = Modifier.size(150.dp, 135.dp)
        ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                painter, "statistics"
            )

            Text(overview,
                modifier = Modifier.padding(top = 5.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold)

            Text(description,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500)

            Text(subdescription,
                color = Color.Red,
                fontSize = 12.sp)
        }
    }
}

@Composable
fun WeeklyBar(
    fillHeight : Int,
    label : String
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.width(28.dp)
                .height(120.dp)
                .background(Color.White,
                    shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(fillHeight.dp)
                    .background(Color.Black,
                        shape = RoundedCornerShape(12.dp))
            ) { }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(label)
    }
}

@Preview
@Composable
fun StatisticsPreview() {
    StatisticsBody()
}