package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.LocalSize
import androidx.compose.ui.unit.DpSize
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
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.ColorFilter
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.size
import com.example.studymateandroidapp.R
import androidx.glance.text.TextStyle
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.data.model.Exam
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ExamWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(100.dp, 100.dp),
            DpSize(200.dp, 120.dp),
            DpSize(300.dp, 200.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = StudyPlannerDatabase.getInstance(context)
        val examDao = database.examDao()
        
        val allExams = examDao.getAllExams().first()
        val now = System.currentTimeMillis()
        val upcomingExams = allExams.filter { it.examDate >= now }
            .sortedBy { it.examDate }

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                ExamWidgetContent(
                    upcomingExams = upcomingExams,
                    widgetSize = size,
                )
            }
        }
    }

    @Composable
    private fun ExamWidgetContent(
        upcomingExams: List<Exam>,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 150.dp || widgetSize.height < 120.dp
        
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
                    provider = ImageProvider(R.drawable.exams),
                    contentDescription = null,
                    modifier = GlanceModifier.size(if (isSmall) 18.dp else 20.dp),
                    colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                )
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                Text(
                    text = "Exams",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 16.sp else 18.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            
            Spacer(modifier = GlanceModifier.height(12.dp))

            if (upcomingExams.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming exams",
                        style = TextStyle(
                            fontSize = 13.sp, 
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            } else {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                ) {
                    val displayCount = if (widgetSize.height > 160.dp) 3 else 1
                    upcomingExams.take(displayCount).forEachIndexed { index, exam ->
                        ExamItem(exam, isSmall)
                        if (index < displayCount - 1 && index < upcomingExams.size - 1) {
                            Spacer(modifier = GlanceModifier.height(8.dp))
                        }
                    }
                }
            }

            if (widgetSize.height > 180.dp) {
                Spacer(modifier = GlanceModifier.height(8.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(ColorProvider(day = Color.Black, night = Color.Black))
                            .cornerRadius(8.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable(actionStartActivity<MainActivity>())
                    ) {
                        Text(
                            text = "View All Exams",
                            style = TextStyle(
                                color = ColorProvider(day = Color.White, night = Color.White),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ExamItem(exam: Exam, isSmall: Boolean) {
        val daysLeft = ChronoUnit.DAYS.between(
            LocalDate.now(),
            Instant.ofEpochMilli(exam.examDate).atZone(ZoneId.systemDefault()).toLocalDate()
        )

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFFF5F5F5)))
                .cornerRadius(8.dp)
                .padding(10.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = exam.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isSmall) 13.sp else 15.sp,
                            color = ColorProvider(day = Color.Black, night = Color.Black)
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = exam.subject,
                        style = TextStyle(
                            fontSize = if (isSmall) 11.sp else 12.sp, 
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        ),
                        maxLines = 1
                    )
                }

                Text(
                    text = when {
                        daysLeft == 0L -> "Today"
                        daysLeft == 1L -> "Tomw"
                        daysLeft < 0L -> "Past"
                        else -> "${daysLeft}d"
                    },
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 12.sp else 14.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    )
                )
            }
        }
    }
}
