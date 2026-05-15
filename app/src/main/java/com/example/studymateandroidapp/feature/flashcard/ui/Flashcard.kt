package com.example.studymateandroidapp.feature.flashcard.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.studymateandroidapp.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FlashCard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardScreen()

        }
    }
}
@Composable
fun FlashcardScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        floatingActionButton = {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add Flashcard",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ){ paddingValues ->

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 48.dp, start = 12.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically


        ) {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Flashcard Decks",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 40.dp)

        ) {
            Spacer(modifier = Modifier.height(100.dp))



            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                border = BorderStroke(0.1.dp, Color.LightGray)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Image(
                            painter = painterResource(id = R.drawable.flashcard),
                            contentDescription = "Study Illustration",
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Study All Due Cards",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "3 cards ready for review",
                            fontSize = 12.sp,
                            color = Color(0xFF913A32)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.LightGray, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {  }) {
                            Icon(
                               painter = painterResource( id = R.drawable.start),
                                contentDescription = "Play",
                                modifier = Modifier.size(32.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    count = "4",
                    label = "Total\ncards",
                    backgroundColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    count = "3",
                    label = "Total\nto review",
                    backgroundColor = Color(0xFFFCE4EC),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    count = "1",
                    label = "Decks\nsubjects",
                    backgroundColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text= "Flashcards for this Exam",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))
            val flashcard = listOf (
                FlashcardData("What is pass in Python?", "a placeholder that does nothing."),
                FlashcardData("Who is created java?", "James Gosling"),
                FlashcardData("Who is created java?", "James Gosling")
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flashcard) { card ->
                    FlashcardItem(card = card)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) } // Space for FAB
            }




        }

    }
}

@Composable
fun FlashcardItem(card: FlashcardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.answer,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(
             painter = painterResource(R.drawable.delete),
                contentDescription = "Delete",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
  }
data class FlashcardData(val question: String, val answer: String)


@Composable
fun StatItem(count: String, label: String, backgroundColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp
            )
        }
    }

}

@Preview
@Composable
fun FlashcardPreview() {
    FlashcardScreen()
}