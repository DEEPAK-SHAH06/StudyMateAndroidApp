package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
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
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.model.Flashcard

class FlashcardWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(100.dp, 100.dp),
            DpSize(200.dp, 120.dp),
            DpSize(300.dp, 200.dp)
        )
    )

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
            
            val currentFlashcard = if (flashcardId != -1L) {
                flashcards.find { it.id == flashcardId } ?: flashcards.lastOrNull()
            } else {
                flashcards.lastOrNull()
            }

            val size = LocalSize.current
            GlanceTheme {
                FlashcardContent(
                    flashcard = currentFlashcard,
                    showAnswer = showAnswer,
                    widgetSize = size
                )
            }
        }
    }

    @Composable
    private fun FlashcardContent(
        flashcard: Flashcard?,
        showAnswer: Boolean,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 150.dp || widgetSize.height < 120.dp

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(ColorProvider(day = Color.White, night = Color.White))
                .cornerRadius(16.dp)
                .padding(12.dp),
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
                    modifier = GlanceModifier.size(if (isSmall) 18.dp else 22.dp),
                    colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                )
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                Text(
                    text = "Flashcard",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 14.sp else 16.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                if (!isSmall && flashcard != null) {
                    Text(
                        text = if (showAnswer) "Answer" else "Question",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            if (flashcard != null) {
                // Card Area
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .background(ColorProvider(day = Color(0xFFF9F9F9), night = Color(0xFFF9F9F9)))
                        .cornerRadius(12.dp)
                        .clickable(actionRunCallback<ToggleAnswerActionCallback>())
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showAnswer) flashcard.answer else flashcard.question,
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = if (isSmall) 14.sp else 16.sp,
                                color = ColorProvider(day = Color.Black, night = Color.Black)
                            ),
                            maxLines = if (widgetSize.height > 150.dp) 5 else 3
                        )
                        
                        if (widgetSize.height > 140.dp) {
                            Spacer(modifier = GlanceModifier.height(8.dp))
                            Text(
                                text = "Tap to flip",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = ColorProvider(day = Color.LightGray, night = Color.LightGray)
                                )
                            )
                        }
                    }
                }

                if (widgetSize.height > 160.dp) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Next button
                        Box(
                            modifier = GlanceModifier
                                .size(32.dp)
                                .background(ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFFF5F5F5)))
                                .cornerRadius(16.dp)
                                .clickable(actionRunCallback<NextFlashcardActionCallback>()),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.next_arrow),
                                contentDescription = "Next",
                                modifier = GlanceModifier.size(16.dp),
                                colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                            )
                        }
                        
                        Spacer(modifier = GlanceModifier.width(8.dp))

                        // Open App Button
                        Box(
                            modifier = GlanceModifier
                                .background(ColorProvider(day = Color.Black, night = Color.Black))
                                .cornerRadius(8.dp)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable(actionStartActivity<MainActivity>())
                        ) {
                            Text(
                                text = "Open",
                                style = TextStyle(
                                    color = ColorProvider(day = Color.White, night = Color.White),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No flashcards found",
                        style = TextStyle(
                            fontSize = 13.sp, 
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }

                // Open App Button
                Box(
                    modifier = GlanceModifier
                        .background(ColorProvider(day = Color.Black, night = Color.Black))
                        .cornerRadius(8.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable(actionStartActivity<MainActivity>())
                ) {
                    Text(
                        text = "Create Flashcards",
                        style = TextStyle(
                            color = ColorProvider(day = Color.White, night = Color.White),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
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
