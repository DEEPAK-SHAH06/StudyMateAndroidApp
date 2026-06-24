package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.ColorFilter
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.action.clickable
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.currentState
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.model.Flashcard

class FlashcardWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val FlashcardIdKey = longPreferencesKey("flashcard_id")
        val ShowAnswerKey = booleanPreferencesKey("show_answer")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = StudyPlannerDatabase.getInstance(context)
        val flashcardDao = database.flashcardDao()
        val flashcards = flashcardDao.getAllFlashcardsList()

        provideContent {
            val prefs = currentState<Preferences>()
            val flashcardId = prefs[FlashcardIdKey] ?: -1L
            val showAnswer = prefs[ShowAnswerKey] ?: false
            
            // If no flashcard is selected or the current one is gone, pick a new one
            val currentFlashcard = if (flashcardId != -1L) {
                flashcards.find { it.id == flashcardId } ?: flashcards.randomOrNull()
            } else {
                flashcards.randomOrNull()
            }

            GlanceTheme {
                FlashcardContent(
                    flashcard = currentFlashcard,
                    showAnswer = showAnswer
                )
            }
        }
    }

    @Composable
    private fun FlashcardContent(
        flashcard: Flashcard?,
        showAnswer: Boolean
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(ColorProvider(day = Color.White, night = Color.White))
                .cornerRadius(16.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.flashcard),
                    contentDescription = null,
                    modifier = GlanceModifier.size(30.dp),
                    colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                )
                
                Spacer(modifier = GlanceModifier.width(10.dp))
                
                Text(
                    text = "Flashcard",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            if (flashcard != null) {
                // Card Area
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .background(ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFFF5F5F5)))
                        .cornerRadius(12.dp)
                        .clickable(actionRunCallback<ToggleAnswerActionCallback>())
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showAnswer) "Answer" else "Question",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = ColorProvider(day = Color.Gray, night = Color.Gray)
                            )
                        )
                        
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        
                        Text(
                            text = if (showAnswer) flashcard.answer else flashcard.question,
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = ColorProvider(day = Color.Black, night = Color.Black)
                            ),
                            maxLines = 4
                        )
                        
                        Spacer(modifier = GlanceModifier.height(14.dp))
                        
                        Text(
                            text = "(Tap to flip)",
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = ColorProvider(day = Color.LightGray, night = Color.LightGray)
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Bottom Action Bar
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(25.dp)
                            .background(ColorProvider(day = Color.White, night = Color.White))
                            .cornerRadius(20.dp)
                            .clickable(actionRunCallback<NextFlashcardActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.next_arrow),
                            contentDescription = "Next",
                            modifier = GlanceModifier.size(20.dp),
                            colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                        )
                    }
                }
            } else {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No flashcards found.\nCreate some in the app!",
                        style = TextStyle(
                            fontSize = 14.sp, 
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
                
                Button(
                    text = "Open App",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }
}

class ToggleAnswerActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[FlashcardWidget.ShowAnswerKey] ?: false
            prefs.toMutablePreferences().apply {
                this[FlashcardWidget.ShowAnswerKey] = !current
            }
        }
        FlashcardWidget().update(context, glanceId)
    }
}

class NextFlashcardActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val database = StudyPlannerDatabase.getInstance(context)
        val flashcards = database.flashcardDao().getAllFlashcardsList()
        val nextCard = flashcards.randomOrNull()

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                if (nextCard != null) {
                    set(FlashcardWidget.FlashcardIdKey, nextCard.id)
                }
                set(FlashcardWidget.ShowAnswerKey, false)
            }
        }
        FlashcardWidget().update(context, glanceId)
    }
}
