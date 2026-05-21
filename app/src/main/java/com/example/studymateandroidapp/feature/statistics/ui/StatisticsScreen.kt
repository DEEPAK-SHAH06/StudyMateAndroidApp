package com.example.studymateandroidapp.feature.statistics.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.studymateandroidapp.R

@Composable
fun StatisticsScreen() {
    val totalTasks = 8
    val completedTasks = 4
    val taskPercentage = (completedTasks * 100) / totalTasks

    val todayStudyMinutes = 125
    val todayStudy = "${todayStudyMinutes / 60}h ${todayStudyMinutes % 60}m"

    val weeklyStudyMinutes = listOf(
        20,
        45,
        70,
        10,
        90,
        30,
        50
    )

    val goalsMet = 2

    val totalWeekMinutes = weeklyStudyMinutes.sum()
    val weeklyStudy = "${totalWeekMinutes / 60}h ${totalWeekMinutes % 60}m"

    val averageMinutes = totalWeekMinutes / weeklyStudyMinutes.size
    val averageStudy = "${averageMinutes / 60}h ${averageMinutes % 60}m/day"

    val labels = listOf(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    )

    val maxMinutes = weeklyStudyMinutes.max()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = Color.White)
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
                    "Statistics",
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
                    horizontalArrangement = Arrangement.spacedBy(space = 30.dp,
                        alignment = Alignment.CenterHorizontally)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        painter = painterResource(R.drawable.task_done),
                        overview = "$completedTasks/$totalTasks",
                        description = "Task Done",
                        subdescription = "$taskPercentage%"
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        painter = painterResource(R.drawable.total),
                        overview = todayStudy,
                        description = "Total Study",
                        subdescription = ""
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 30.dp,
                        alignment = Alignment.CenterHorizontally)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        painter = painterResource(R.drawable.task_done),
                        overview = "$goalsMet",
                        description = "Goals Met",
                        subdescription = ""
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        painter = painterResource(R.drawable.total),
                        overview = weeklyStudy,
                        description = "This Week",
                        subdescription = "Avg: $averageStudy"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Text("Weekly Overview",
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
                        labels.forEachIndexed { index, label ->
                            val normalizedHeight =
                                if (maxMinutes > 0)
                                    (weeklyStudyMinutes[index] * 100) / maxMinutes
                                else 0

                            WeeklyBar(
                                fillHeight = normalizedHeight,
                                label = label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    painter : Painter,
    overview : String,
    description : String,
    subdescription : String
) {
    OutlinedCard(modifier = Modifier.height(135.dp)
        ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                painter = painter,
                contentDescription = "statistics"
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
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(label)
    }
}

@Preview
@Composable
fun StatisticsPreview() {
    StatisticsScreen()
}