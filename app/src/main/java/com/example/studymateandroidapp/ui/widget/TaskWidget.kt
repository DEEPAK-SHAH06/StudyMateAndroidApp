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
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Jetpack Glance Widget for Study Planner Tasks.
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
        
        // Fetch tasks for the upcoming week
        val today = LocalDate.now()
        val nextWeek = today.plusDays(7)
        val allTasks = taskDao.getAllTasksList()
        
        val weekTasks = allTasks.filter { 
            val date = it.dueDate
            date != null && !date.isBefore(today) && !date.isAfter(nextWeek)
        }
        
        val pendingTasks = weekTasks.filter { !it.isCompleted }
            .sortedWith(compareBy({ it.dueDate }, { it.dueTime }))
            
        val totalTasks = weekTasks.size
        val completedTasks = weekTasks.count { it.isCompleted }
        val progress = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks * 100).toInt() else 0

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                TaskWidgetContent(
                    pendingTasks = pendingTasks,
                    progress = progress,
                    totalTasks = totalTasks,
                    completedTasks = completedTasks,
                    widgetSize = size
                )
            }
        }
    }

    @Composable
    private fun TaskWidgetContent(
        pendingTasks: List<Task>,
        progress: Int,
        totalTasks: Int,
        completedTasks: Int,
        widgetSize: DpSize
    ) {
        val isSmall = widgetSize.width < 150.dp || widgetSize.height < 120.dp
        val today = LocalDate.now()
        val nextWeek = today.plusDays(7)
        val dateRangeText = "${today.format(DateTimeFormatter.ofPattern("MMM d"))} - ${nextWeek.format(DateTimeFormatter.ofPattern("MMM d"))}"

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
                    provider = ImageProvider(R.drawable.createtask_dark),
                    contentDescription = null,
                    modifier = GlanceModifier.size(if (isSmall) 18.dp else 22.dp),
                    colorFilter = ColorFilter.tint(ColorProvider(day = Color.Black, night = Color.Black))
                )
                
                Spacer(modifier = GlanceModifier.width(8.dp))
                
                Text(
                    text = "Tasks",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 14.sp else 16.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                if (!isSmall) {
                    Text(
                        text = dateRangeText,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Progress Bar (Simplified)
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(if (isSmall) 4.dp else 6.dp)
                    .background(ColorProvider(day = Color(0xFFF0F0F0), night = Color(0xFFF0F0F0)))
                    .cornerRadius(3.dp)
            ) {
                if (progress > 0) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(if (isSmall) 4.dp else 6.dp)
                            .background(ColorProvider(day = Color.Black, night = Color.Black))
                            .cornerRadius(3.dp),
                        content = {}
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(4.dp))
            
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = "$progress% Done",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(day = Color.DarkGray, night = Color.DarkGray)
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                if (totalTasks > 0) {
                    Text(
                        text = "$completedTasks/$totalTasks",
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Task List
            if (pendingTasks.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (totalTasks > 0) "All caught up! 🎉" else "No tasks for this week",
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
                    val displayCount = when {
                        widgetSize.height > 180.dp -> 3
                        widgetSize.height > 140.dp -> 2
                        else -> 1
                    }
                    
                    pendingTasks.take(displayCount).forEachIndexed { index, task ->
                        TaskItem(task, isSmall)
                        if (index < displayCount - 1 && index < pendingTasks.size - 1) {
                            Spacer(modifier = GlanceModifier.height(6.dp))
                        }
                    }
                }
            }

            // Bottom Actions
            if (widgetSize.height > 160.dp) {
                Spacer(modifier = GlanceModifier.height(8.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Refresh button
                    Box(
                        modifier = GlanceModifier
                            .size(32.dp)
                            .background(ColorProvider(day = Color(0xFFF5F5F5), night = Color(0xFFF5F5F5)))
                            .cornerRadius(16.dp)
                            .clickable(actionRunCallback<RefreshActionCallback>()),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.again),
                            contentDescription = "Refresh",
                            modifier = GlanceModifier.size(14.dp),
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
        }
    }

    @Composable
    private fun TaskItem(task: Task, isSmall: Boolean) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(day = Color(0xFFF9F9F9), night = Color(0xFFF9F9F9)))
                .cornerRadius(8.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .size(16.dp)
                    .background(ColorProvider(day = Color.White, night = Color.White))
                    .cornerRadius(4.dp)
                    .padding(2.dp)
            ) {
                // Checkbox-like appearance
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(ColorProvider(day = Color.LightGray.copy(alpha = 0.2f), night = Color.LightGray.copy(alpha = 0.2f)))
                        .cornerRadius(2.dp)
                ) {}
            }
            
            Spacer(modifier = GlanceModifier.width(8.dp))
            
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = task.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = if (isSmall) 12.sp else 13.sp,
                        color = ColorProvider(day = Color.Black, night = Color.Black)
                    ),
                    maxLines = 1
                )
                if (!isSmall && task.subjectTag != null) {
                    Text(
                        text = task.subjectTag,
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = ColorProvider(day = Color.Gray, night = Color.Gray)
                        )
                    )
                }
            }

            if (!isSmall && task.dueDate != null) {
                Text(
                    text = if (task.dueDate == LocalDate.now() && task.dueTime != null) {
                        task.dueTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    } else {
                        task.dueDate.format(DateTimeFormatter.ofPattern("MMM d"))
                    },
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = ColorProvider(day = Color.Gray, night = Color.Gray)
                    )
                )
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
        StudyPlannerWidget().update(context, glanceId)
    }
}
