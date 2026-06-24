package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
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
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.StudyMateApplication
import com.example.studymateandroidapp.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Jetpack Glance Widget for Study Statistics (Weekly Chart).
 */
class StatisticsWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(150.dp, 120.dp),
            DpSize(250.dp, 150.dp),
            DpSize(300.dp, 200.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as StudyMateApplication
        val statisticsRepository = app.statisticsRepository
        
        val dailyData = try {
            statisticsRepository.getDailyStudyData(7).first()
        } catch (e: Exception) {
            emptyList()
        }

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                StatisticsWidgetContent(
                    data = dailyData,
                    widgetSize = size
                )
            }
        }
    }

    @Composable
    private fun StatisticsWidgetContent(
        data: List<StatisticsRepository.DailyStudyData>,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 200.dp || widgetSize.height < 140.dp
        val totalStudySeconds = data.sumOf { it.studySeconds }
        val hours = totalStudySeconds / 3600
        val minutes = (totalStudySeconds % 3600) / 60

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
                    provider = ImageProvider(R.drawable.reflection),
                    contentDescription = null,
                    modifier = GlanceModifier.size(if (isSmall) 18.dp else 22.dp),
                    colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                )
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                Text(
                    text = "Activity",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 14.sp else 16.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                if (!isSmall) {
                    Text(
                        text = if (hours > 0) "${hours}h ${minutes}m total" else "${minutes}m total",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            }

            if (data.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start studying to see stats!",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            } else {
                val maxSeconds = (data.maxOfOrNull { it.studySeconds } ?: 1).coerceAtLeast(1)
                
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Adjust bar area height based on available space
                    val headerHeight = if (isSmall) 30.dp else 40.dp
                    val labelsHeight = 20.dp
                    val barAreaHeight = (widgetSize.height - headerHeight - labelsHeight - 24.dp).coerceAtLeast(40.dp)
                    
                    data.forEach { point ->
                        val barHeightWeight = point.studySeconds.toFloat() / maxSeconds
                        val barHeight = barAreaHeight * barHeightWeight.coerceIn(0.05f, 1.0f)
                        val isToday = point.date == LocalDate.now()
                        
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Value text above bar (only if there's enough height)
                            if (widgetSize.height > 180.dp && point.studySeconds > 0) {
                                Text(
                                    text = "${point.studySeconds / 60}m",
                                    style = TextStyle(fontSize = 9.sp, color = ColorProvider(day = Color.LightGray, night = Color.LightGray))
                                )
                                Spacer(modifier = GlanceModifier.height(2.dp))
                            }

                            Box(
                                modifier = GlanceModifier
                                    .width(if (isSmall) 12.dp else 18.dp)
                                    .height(barHeight)
                                    .background(
                                        if (isToday) ColorProvider(day = Color.Black, night = Color.Black)
                                        else ColorProvider(day = Color(0xFFF0F0F0), night = Color(0xFFF0F0F0))
                                    )
                                    .cornerRadius(if (isSmall) 3.dp else 4.dp)
                            ) {}
                            
                            Spacer(modifier = GlanceModifier.height(6.dp))
                            
                            // Day Label
                            Text(
                                text = point.date.dayOfWeek.name.take(1),
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) ColorProvider(day = Color.Black, night = Color.Black)
                                           else ColorProvider(day = Color.Gray, night = Color.Gray)
                                )
                            )
                        }
                    }
                }
            }

        }
    }
}
