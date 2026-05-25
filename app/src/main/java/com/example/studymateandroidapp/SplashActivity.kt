package com.example.studymateandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.ui.screens.CalendarScreen
import com.example.studymateandroidapp.ui.screens.DashboardScreen
import com.example.studymateandroidapp.ui.navigation.Screen
//import com.example.test101softwaredevelopment.ui.theme.Test101SoftwareDevelopmentTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody(){
    val context = LocalContext.current
    //val activity = context.findActivity()
    LaunchedEffect(Unit) {

        delay(3000)
        val sharedPreferences = context.getSharedPreferences(
            "User",
            Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", true)
        if (isLoggedIn){
            val intent = Intent(context,  MainActivity::class.java)
            context.startActivity(intent)
        }else{
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.achievement),
            contentDescription = null,
            modifier = Modifier.height(60.dp).width(60.dp)
        )
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun SplashView() {
    SplashBody()
}