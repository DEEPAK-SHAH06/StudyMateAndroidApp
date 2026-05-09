package com.example.studymateandroidapp.feature.task.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.feature.exam.ui.Exam
import com.example.studymateandroidapp.feature.timer.ui.Timer
import com.example.studymateandroidapp.ui.theme.StudyMateAndroidAppTheme

class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyMateAndroidAppTheme {
                TaskScreen()
            }
        }
    }
}

@Composable
fun TaskScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf(
        stringResource(R.string.pending),
        stringResource(R.string.completed),
        stringResource(R.string.overdue)
    )
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { TaskBottomNavigation() }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                TaskTopBar()

                Spacer(modifier = Modifier.height(32.dp))
                Column {
                    Text(
                        text = stringResource(R.string.my_tasks),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        lineHeight = 56.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.manage_study_load),
                        fontSize = 18.sp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )

                Spacer(modifier = Modifier.height(28.dp))
                FilterChips(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                Spacer(modifier = Modifier.height(48.dp))
                SectionHeader(
                    title = stringResource(R.string.today) + " • •",
                    subtitle = stringResource(R.string.tasks_remaining, 2)
                )

                Spacer(modifier = Modifier.height(24.dp))
                TaskItemView(
                    task = TaskItem(
                        "Cognitive Psychology Thesis Draft",
                        "Finalize the literature review section.",
                        "PSYCHOLOGY",
                        Color(0xFFB33A3A),
                        false,
                        "1:00 PM"
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                TaskItemView(
                    task = TaskItem(
                        "Advanced Calculus Problem Set 3",
                        "Solve problems 1-15 from Chapter 4.",
                        "MATHEMATICS",
                        Color(0xFFE6C35C),
                        false,
                        "2:45 PM"
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))
                SectionHeader(
                    title = stringResource(R.string.tomorrow) + " •",
                    subtitle = ""
                )
                
                Spacer(modifier = Modifier.height(120.dp))
            }

            TaskFAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 100.dp)
            )
        }
    }
}

@Composable
fun TaskTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.achievement),
            contentDescription = "Achievements",
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified
        )
        Icon(
            painter = painterResource(id = R.drawable.stat),
            contentDescription = "Statistics",
            modifier = Modifier.size(32.dp),
            tint = Color.Black
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp)),
        placeholder = {
            Text(
                text = "Search tasks, subjects, or",
                fontSize = 17.sp,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray.copy(alpha = 0.7f)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF7F8FA),
            unfocusedContainerColor = Color(0xFFF7F8FA),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        singleLine = true
    )
}

@Composable
fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(24.dp),
                color = if (isSelected) Color.Black else Color(0xFFF2F4F7)
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun TaskItemView(
    task: TaskItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.5.dp, Color(0xFFF0F2F5))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFE0E4E9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        painter = painterResource(id = R.drawable.done),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    color = Color.Black
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            fontSize = 15.sp,
                            color = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.notes),
                        contentDescription = "More",
                        modifier = Modifier.size(22.dp),
                        tint = Color.Gray.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = task.tagColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = task.tag,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.timer),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.time,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun TaskScreenPreview() {
    StudyMateAndroidAppTheme {
        TaskScreen()
    }
}

@Composable
fun TaskFAB(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(92.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(4.dp, Color.Black)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .border(2.5.dp, Color.Black, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Task",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun TaskBottomNavigation() {
    val context = LocalContext.current
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(88.dp)
    ) {
        val items = listOf(
            Triple(R.drawable.home, "Home", false),
            Triple(R.drawable.task, "Task", true),
            Triple(R.drawable.exams, "Exams", false),
            Triple(R.drawable.timer, "Timer", false),
            Triple(R.drawable.lock, "Settings", false)
        )

        items.forEach { (iconRes, label, isSelected) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = isSelected,
                onClick = {
                    when (label) {
                        "Exams" -> context.startActivity(Intent(context, Exam::class.java))
                        "Home" -> context.startActivity(Intent(context, com.example.studymateandroidapp.MainActivity::class.java))
                        "Timer" -> context.startActivity(Intent(context, Timer::class.java))
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Gray.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class TaskItem(
    val title: String,
    val description: String = "",
    val tag: String,
    val tagColor: Color,
    val isCompleted: Boolean,
    val time: String
)
