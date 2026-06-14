package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .background(ColorProvider(day = Color.Black, night = Color.Black))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = "Quick Review",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ColorProvider(day = Color.White, night = Color.White)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            if (flashcard != null) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .background(ColorProvider(day = Color.DarkGray, night = Color.DarkGray))
                        .clickable(actionRunCallback<ToggleAnswerActionCallback>())
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (showAnswer) "A: ${flashcard.answer}" else "Q: ${flashcard.question}",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = ColorProvider(day = Color.White, night = Color.White)
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(ColorProvider(day = Color.White, night = Color.White))
                            .cornerRadius(18.dp)
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
                        .defaultWeight()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No flashcards found. Create some in the app!",
                        style = TextStyle(fontSize = 14.sp, color = GlanceTheme.colors.onSurfaceVariant)
                    )
                }
                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(ColorProvider(day = Color.Black, night = Color.Black))
                        .cornerRadius(18.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.outline_exit_to_app_24),
                        contentDescription = "Open",
                        modifier = GlanceModifier.size(20.dp),
                        colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
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
