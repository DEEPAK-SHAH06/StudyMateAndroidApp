package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.color.ColorProvider
import com.example.studymateandroidapp.StudyMateApplication
import com.example.studymateandroidapp.R
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class HabitWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(180.dp, 120.dp),
            DpSize(300.dp, 150.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as StudyMateApplication
        val statisticsRepository = app.statisticsRepository
        
        val streakCount = try { statisticsRepository.getStreak().first() } catch (_: Exception) { 0 }
        val bestStreak = try { statisticsRepository.getBestStreak().first() } catch (_: Exception) { 0 }
        val weeklyStatus = try { statisticsRepository.getWeeklyStreakStatus().first() } catch (_: Exception) { List(7) { false } }

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                HabitWidgetContent(
                    streakCount = streakCount,
                    bestStreak = bestStreak,
                    weeklyStatus = weeklyStatus,
                    widgetSize = size
                )
            }
        }
    }

    @Composable
    private fun HabitWidgetContent(
        streakCount: Int,
        bestStreak: Int,
        weeklyStatus: List<Boolean>,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 250.dp
        val days = listOf("S", "M", "T", "W", "T", "F", "S")
        val todayIndex = LocalDate.now().dayOfWeek.value % 7

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
                Text(
                    text = "Study Habit",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 16.sp else 18.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                
                Row(
                    modifier = GlanceModifier
                        .background(ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFFF5F5F5)))
                        .cornerRadius(12.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥 $streakCount",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ColorProvider(day = Color.Black, night = Color.Black)
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))
            
            if (!isSmall) {
                Text(
                    text = "You're on a $streakCount day streak!",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(day = Color.Gray, night = Color.Gray)
                    ),
                    modifier = GlanceModifier.fillMaxWidth()
                )
                Spacer(modifier = GlanceModifier.height(16.dp))
            }

            // Habit dots
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEachIndexed { index, day ->
                    val isActive = weeklyStatus.getOrElse(index) { false }
                    val isToday = index == todayIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.defaultWeight()
                    ) {
                        Text(
                            text = day,
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) ColorProvider(day = Color.Black, night = Color.Black) 
                                        else ColorProvider(day = Color.LightGray, night = Color.LightGray)
                            )
                        )
                        
                        Spacer(modifier = GlanceModifier.height(6.dp))
                        
                        Box(
                            modifier = GlanceModifier
                                .size(if (isSmall) 24.dp else 32.dp)
                                .cornerRadius(if (isSmall) 12.dp else 16.dp)
                                .background(
                                    if (isActive) ColorProvider(day = Color.Black, night = Color.Black)
                                    else ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFFF5F5F5))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isActive) {
                                Text(
                                    text = "●",
                                    style = TextStyle(
                                        color = ColorProvider(day = Color.White, night = Color.White),
                                        fontSize = if (isSmall) 10.sp else 12.sp
                                    )
                                )
                            } else if (isToday) {
                                Box(
                                    modifier = GlanceModifier
                                        .size(6.dp)
                                        .cornerRadius(3.dp)
                                        .background(ColorProvider(day = Color.LightGray, night = Color.LightGray))
                                ) {}
                            }
                        }
                    }
                }
            }
            
            if (!isSmall) {
                Spacer(modifier = GlanceModifier.height(12.dp))
                Text(
                    text = "Best streak: $bestStreak",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(day = Color.Gray, night = Color.Gray)
                    ),
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
        }
    }
}
