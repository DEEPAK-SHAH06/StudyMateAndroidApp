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
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.action.clickable
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
                    widgetSize = size
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
                .background(ColorProvider(day = Color.White, night = Color.White))
                .padding(if (isSmall) 8.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Exams",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 14.sp else 16.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            Spacer(modifier = GlanceModifier.height(if (isSmall) 4.dp else 8.dp))

            if (upcomingExams.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming exams",
                        style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
                    )
                }
            } else {
                val nextExam = upcomingExams.first()
                val daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    Instant.ofEpochMilli(nextExam.examDate).atZone(ZoneId.systemDefault()).toLocalDate()
                )

                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(day = Color.LightGray, night = Color.LightGray))
                        .padding(8.dp)
                ) {
                    Text(
                        text = nextExam.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ColorProvider(day = Color.Black, night = Color.Black)
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = nextExam.subject,
                        style = TextStyle(fontSize = 12.sp, color = ColorProvider(day = Color.DarkGray, night = Color.DarkGray)),
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = when (daysLeft) {
                            0L -> "Today!"
                            1L -> "Tomorrow"
                            else -> "In $daysLeft days"
                        },
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ColorProvider(day = Color.Red, night = Color.Red)
                        )
                    )
                }

                if (widgetSize.height > 120.dp && upcomingExams.size > 1) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    val secondExam = upcomingExams[1]
                    Text(
                        text = "Next: ${secondExam.title}",
                        style = TextStyle(fontSize = 11.sp, color = ColorProvider(day = Color.DarkGray, night = Color.DarkGray)),
                        maxLines = 1,
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            if (widgetSize.height > 60.dp) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (widgetSize.width > 150.dp) {
                        Box(
                            modifier = GlanceModifier
                                .size(36.dp)
                                .background(ColorProvider(day = Color.White, night = Color.White))
                                .cornerRadius(18.dp)
                                .clickable(androidx.glance.appwidget.action.actionRunCallback<RefreshExamsActionCallback>()),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.again),
                                contentDescription = "Refresh",
                                modifier = GlanceModifier.size(20.dp),
                                colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                            )
                        }
                        Spacer(modifier = GlanceModifier.width(16.dp))
                    }
                    Box(
                        modifier = GlanceModifier
                            .size(36.dp)
                            .background(ColorProvider(day = Color.White, night = Color.White))
                            .cornerRadius(18.dp)
                            .clickable(actionStartActivity<MainActivity>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.outline_exit_to_app_24),
                            contentDescription = "Open App",
                            modifier = GlanceModifier.size(20.dp),
                            colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                        )
                    }
                }
            }
        }
    }
}

class RefreshExamsActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        ExamWidget().update(context, glanceId)
    }
}
