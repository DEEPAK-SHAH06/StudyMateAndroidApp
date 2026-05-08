package com.example.studymateandroidapp.feature.settings.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.navigation.BottomNavBar

class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingBody()
        }
    }
}

@Composable
fun SettingBody() {
    var name : String = "username"
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier.padding(innerpadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.achievements),
                        contentDescription = "achievements"
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.statistics),
                        contentDescription = "achievements"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "profile picture",
                    modifier = Modifier.size(140.dp)
                )

                Text(
                    name,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(15.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column() {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.LightGray.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(15.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(R.drawable.sync_off), null
                                )

                                Spacer(Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Not Synced",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        "Sign in to keep your data safe",
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }

                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                ) {
                                    Icon(
                                        painterResource(R.drawable.signin),
                                        "sign in"
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text("Sign In")
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { },
                    modifier = Modifier.padding(vertical = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.edit),
                        "edit profile"
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Edit Your Profile")
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 35.dp)
            ) {

                Text(
                    "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                NotifCard(
                    "Task",
                    "Get notified before task deadline",
                    painterResource(R.drawable.on)
                )

                NotifCard(
                    "Exam",
                    "Get alerts 1 and 3 days before exams",
                    painterResource(R.drawable.on)
                )

                NotifCard(
                    "Daily habit",
                    "Daily reminder to keep up your study habit",
                    painterResource(R.drawable.on)
                )

                NotifCard(
                    "Missed task",
                    "Alert if you have incomplete tasks at the end of the day",
                    painterResource(R.drawable.on)
                )

                NotifCard(
                    "Daily goal",
                    "Alert if you haven’t met your daily study goal",
                    painterResource(R.drawable.on)
                )

                NotifCard(
                    "Focus mode",
                    "Pause all notifications during focus time",
                    painterResource(R.drawable.off)
                )
            }
        }
    }
}

@Composable
fun NotifCard(
    heading : String,
    subheading : String,
    painter: Painter
) {
    OutlinedCard (modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(heading,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)

                Text(subheading,
                    color = Color.Gray,
                    fontSize = 10.sp)
            }

            Icon(
                painter, "toggle button"
            )
        }
    }
}

@Preview
@Composable
fun SettingPreview() {
    SettingBody()
}