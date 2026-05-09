package com.example.studymateandroidapp.feature.task.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.ui.theme.StudyMateAndroidAppTheme

class Lockscreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyMateAndroidAppTheme {
                LockscreenBody()
            }
        }
    }
}

@Composable
fun LockscreenBody(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Emoji Illustration
            Icon(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Greeting Text
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.hello_user),
                    style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )
                )

                Text(
                    text = stringResource(id = R.string.welcome_back),
                    style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    ),
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Instruction Text
            Text(
                text = "Please place your finger over the\nsensor",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Fingerprint Button
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
                shadowElevation = 4.dp,
                onClick = {
                    context.startActivity(Intent(context, TaskActivity::class.java))
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.fingerprint),
                        contentDescription = stringResource(id = R.string.fingerprint_instruction),
                        modifier = Modifier.size(56.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
fun LockscreenPreview() {
    StudyMateAndroidAppTheme {
        LockscreenBody()
    }
}