package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
import androidx.glance.LocalSize
import androidx.compose.ui.unit.DpSize
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.appwidget.cornerRadius
import com.example.studymateandroidapp.StudyMateApplication
import com.example.studymateandroidapp.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

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
        val isSmall = widgetSize.width < 200.dp
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(16.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Weekly Study Time",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isSmall) 14.sp else 16.sp,
                    color = GlanceTheme.colors.primary
                ),
                modifier = GlanceModifier.fillMaxWidth()
            )
            
            Spacer(modifier = GlanceModifier.height(8.dp))

            if (data.isEmpty()) {
                Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available", style = TextStyle(fontSize = 12.sp))
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
                    val barAreaHeight = (widgetSize.height - 64.dp).coerceAtLeast(20.dp)
                    
                    data.forEach { point ->
                        val barHeightWeight = point.studySeconds.toFloat() / maxSeconds
                        val barHeight = barAreaHeight * barHeightWeight.coerceIn(0.05f, 1.0f)
                        
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .width(if (isSmall) 12.dp else 20.dp)
                                    .height(barHeight)
                                    .background(if (point.date == LocalDate.now()) GlanceTheme.colors.primary else GlanceTheme.colors.secondaryContainer)
                                    .cornerRadius(4.dp)
                            ) {}
                            
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            
                            // Label
                            Text(
                                text = point.date.dayOfWeek.name.take(1),
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = GlanceTheme.colors.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
