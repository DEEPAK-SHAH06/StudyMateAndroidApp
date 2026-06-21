package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import androidx.compose.ui.graphics.Color
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.model.Goal

/**
 * Jetpack Glance Widget for Study Goals.
 * Displays the most recent or important active goal.
 */
class GoalWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(100.dp, 100.dp),
            DpSize(200.dp, 120.dp),
            DpSize(300.dp, 150.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = StudyPlannerDatabase.getInstance(context)
        val goalDao = database.goalDao()
        
        // Fetch up to 4 active goals
        val goals = try {
            goalDao.getAllGoalsList()
                .filter { it.currentValue < it.targetValue }
                .sortedBy { it.deadline }
                .take(4)
        } catch (_: Exception) {
            emptyList()
        }

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                GoalWidgetContent(
                    goals = goals,
                    widgetSize = size
                )
            }
        }
    }

    @Composable
    private fun GoalWidgetContent(
        goals: List<Goal>,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 150.dp || widgetSize.height < 120.dp
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(ColorProvider(day = Color.White, night = Color.White))
                .cornerRadius(16.dp)
                .padding(if (isSmall) 12.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.achievement),
                    contentDescription = null,
                    modifier = GlanceModifier.size(if (isSmall) 20.dp else 26.dp),
                    colorFilter = androidx.glance.ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                )
                
                Spacer(modifier = GlanceModifier.width(9.dp))
                
                Text(
                    text = "My Goals",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 16.sp else 18.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            
            Spacer(modifier = GlanceModifier.height(8.dp))

            if (goals.isNotEmpty()) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                ) {
                    goals.forEachIndexed { index, goal ->
                        GoalItem(goal, isSmall, widgetSize.width)
                        if (index < goals.size - 1) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                        }
                    }
                }
            } else {
                Box(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active goals",
                        style = TextStyle(fontSize = 13.sp, color = ColorProvider(day = Color.Black, night = Color.Black))
                    )
                }
            }
            
            if (widgetSize.height > 180.dp) {
                Spacer(modifier = GlanceModifier.height(8.dp))
                Button(
                    text = "View All Goals",
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }

    @Composable
    private fun GoalItem(goal: Goal, isSmall: Boolean, availableWidth: androidx.compose.ui.unit.Dp) {
        val progress = if (goal.targetValue > 0) goal.currentValue.toFloat() / goal.targetValue else 0f
        val progressPercent = (progress * 100).toInt()
        
        // Calculate the width of the progress bar
        // Horizontal padding: outer column (12 or 8) + inner item (8)
        val horizontalPadding = (if (isSmall) 8.dp else 12.dp) + 8.dp
        val totalBarWidth = availableWidth - (horizontalPadding * 2)
        val filledWidth = totalBarWidth * progress

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(day = Color.White, night = Color.White))
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = goal.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = if (isSmall) 13.sp else 15.sp,
                            color = ColorProvider(day = Color.Black, night = Color.Black)
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = GlanceModifier.width(4.dp))

                Text(
                    text = "$progressPercent%",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 11.sp else 13.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    )
                )
            }
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Standard progress bar - Black & White
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(if (isSmall) 6.dp else 8.dp)
                    .background(ColorProvider(day = Color.LightGray, night = Color.DarkGray))
                    .cornerRadius(4.dp)
            ) {
                if (progress > 0f) {
                    Box(
                        modifier = GlanceModifier
                            .width(filledWidth)
                            .fillMaxHeight()
                            .background(ColorProvider(day = Color.Black, night = Color.Black))
                            .cornerRadius(4.dp)
                    ) {}
                }
            }
            
            // Subtle separator line
            Spacer(modifier = GlanceModifier.height(8.dp))
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ColorProvider(day = Color.LightGray, night = Color.DarkGray))
            ) {}
        }
    }
}
