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
import androidx.glance.appwidget.SizeMode
import androidx.glance.LocalSize
import androidx.compose.ui.unit.DpSize
import androidx.glance.ButtonDefaults
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
import androidx.glance.text.TextStyle
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.data.model.Task
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.ColorFilter
import androidx.glance.appwidget.cornerRadius
import androidx.glance.action.clickable
import androidx.glance.layout.size
import com.example.studymateandroidapp.R

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Jetpack Glance Widget for Study Planner.
 * Displays today's progress and the next upcoming task.
 */
class StudyPlannerWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(100.dp, 100.dp),
            DpSize(200.dp, 120.dp),
            DpSize(300.dp, 200.dp)
        )
    )


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = StudyPlannerDatabase.getInstance(context)
        val taskDao = database.taskDao()
        
        // Fetch data for the widget from Room Database
        val today = LocalDate.now()
        // Note: In a real app, we might want to use a more optimized query or a Repository
        val todayTasks = taskDao.getAllTasksList().filter { it.dueDate == today }
        val nextTask = todayTasks.filter { !it.isCompleted }
            .sortedBy { it.dueTime }
            .firstOrNull()
            
        val totalTasks = todayTasks.size
        val completedTasks = todayTasks.count { it.isCompleted }
        val progress = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks * 100).toInt() else 0

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                WidgetContent(
                    nextTask = nextTask,
                    progress = progress,
                    remainingTasks = totalTasks - completedTasks,
                    todayDate = today.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())),
                    widgetSize = size
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        nextTask: Task?,
        progress: Int,
        remainingTasks: Int,
        todayDate: String,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 150.dp || widgetSize.height < 120.dp
        
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = Color.Black, night = Color.Black))
                .padding(if (isSmall) 8.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section: App Name and Date
            if (widgetSize.height > 80.dp) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = if (isSmall) "Task" else "Task",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isSmall) 14.sp else 16.sp,
                            color = ColorProvider(day = Color.White, night = Color.White)
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    if (!isSmall) {
                        Text(
                            text = todayDate,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = ColorProvider(day = Color.LightGray, night = Color.LightGray)
                            )
                        )
                    }
                }
                Spacer(modifier = GlanceModifier.height(if (isSmall) 4.dp else 8.dp))
            }

            // Progress Section
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(vertical = if (isSmall) 2.dp else 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSmall) "$progress%" else "Progress: $progress%",
                    style = TextStyle(
                        fontSize = if (isSmall) 12.sp else 13.sp, 
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(day = Color.Cyan, night = Color.Cyan)
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                if (widgetSize.width > 120.dp) {
                    Text(
                        text = "$remainingTasks left",
                        style = TextStyle(fontSize = 11.sp, color = ColorProvider(day = Color.LightGray, night = Color.LightGray))
                    )
                }
            }

            if (widgetSize.height > 100.dp) {
                Spacer(modifier = GlanceModifier.height(if (isSmall) 4.dp else 8.dp))

                // Next Task Card
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(day = Color.DarkGray, night = Color.DarkGray))
                        .padding(if (isSmall) 8.dp else 12.dp)
                ) {
                    if (nextTask != null) {
                        Column {
                            Text(
                                text = nextTask.title,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isSmall) 13.sp else 15.sp,
                                    color = ColorProvider(day = Color.White, night = Color.White)
                                ),
                                maxLines = 1
                            )
                            if (!isSmall || widgetSize.width > 180.dp) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = nextTask.subjectTag ?: "Study",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = ColorProvider(day = Color.LightGray, night = Color.LightGray)
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = if (isSmall) "All done! 🎉" else "No more tasks today! 🎉",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = ColorProvider(day = Color.White, night = Color.White)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // Action Buttons
            if (widgetSize.height > 150.dp || (widgetSize.height > 70.dp && widgetSize.width > 150.dp)) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (widgetSize.width > 180.dp) {
                        Box(
                            modifier = GlanceModifier
                                .size(36.dp)
                                .background(ColorProvider(day = Color.Black, night = Color.Black))
                                .cornerRadius(18.dp)
                                .clickable(actionRunCallback<RefreshActionCallback>()),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.again),
                                contentDescription = "Refresh",
                                modifier = GlanceModifier.size(20.dp),
                                colorFilter = ColorFilter.tint(ColorProvider(day = Color.White, night = Color.White))
                            )
                        }
                    }
                    Spacer(modifier = GlanceModifier.width(16.dp))
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
                            colorFilter = ColorFilter.tint(ColorProvider(day = Color.White, night = Color.White))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Callback to handle the Refresh button click.
 */
class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Manually trigger a widget update to fetch fresh data
        StudyPlannerWidget().update(context, glanceId)
    }
}
