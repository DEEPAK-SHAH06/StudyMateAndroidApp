package com.example.studymateandroidapp.feature.task.ui
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymateandroidapp.MainActivity
import com.example.studymateandroidapp.R


/**
 * Data model for a task item.
 */
data class TaskItem(
    val title: String,
    val description: String = "",
    val tag: String,
    val tagBgColor: Color,
    val tagTextColor: Color,
    val time: String
)

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    TaskScreen()

}

class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskScreen()

        }
    }
}

@Composable
fun TaskScreen() {

    var searchQuery by remember { mutableStateOf("") }

    val filters = listOf(
        "Pending",
        "Completed",
        "Overdue"
    )

    var selectedFilter by remember {
        mutableStateOf(filters[0])
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            TaskBottomNavigation()
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.height(22.dp))

                TaskTopBar()

                Spacer(modifier = Modifier.height(28.dp))

                HeaderSection()

                Spacer(modifier = Modifier.height(12.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilterChips(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = {
                        selectedFilter = it
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                SectionHeader(
                    title = "Today •",
                    subtitle = "2 TASKS REMAINING"
                )

                Spacer(modifier = Modifier.height(10.dp))

                TaskItemView(
                    task = TaskItem(
                        title = "Cognitive Psychology Thesis Draft",
                        description = "",
                        tag = "PSYCHOLOGY",
                        tagBgColor = Color(0xFFE84C4F),
                        tagTextColor = Color.White,
                        time = "1:00 PM"
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                TaskItemView(
                    task = TaskItem(
                        title = "Advanced Calculus Problem Set 3",
                        description = "",
                        tag = "MATHEMATICS",
                        tagBgColor = Color(0xFFEACB57),
                        tagTextColor = Color.White,
                        time = "2:45 PM"
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                SectionHeader(
                    title = "Tomorrow",
                    subtitle = ""
                )

                Spacer(modifier = Modifier.height(10.dp))

                TaskItemView(
                    task = TaskItem(
                        title = "History of Art: Romaine Write",
                        description = "",
                        tag = "HISTORY",
                        tagBgColor = Color(0xFF7FAF59),
                        tagTextColor = Color.White,
                        time = "5:00 PM"
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                PriorityTaskCard()

                Spacer(modifier = Modifier.height(14.dp))

                OverdueMilestoneCard()

                // Added spacer to ensure content is not hidden by the FAB
                Spacer(modifier = Modifier.height(100.dp))
            }

            TaskFAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 16.dp,
                        bottom = 16.dp // Adjusted padding since Scaffold innerPadding already handles bottom bar
                    )
            )
        }
    }
}

@Composable
fun TaskTopBar() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.achievements),
            contentDescription = "Menu",
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )

        Icon(
            painter = painterResource(id = R.drawable.statistics),
            contentDescription = "Menu",
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
    }
}

@Composable
fun HeaderSection() {

    Column {

        Text(
            text = "My Tasks",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Manage your study load with intentionally.",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .width(8.dp)
            .height(49.dp),
        placeholder = {

            Text(
                text = "Search tasks, subjects, or deadlines.........",
                fontSize = 14.sp,
                color = Color.Gray
            )
        },
        leadingIcon = {

            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFDCDCDC),
            unfocusedContainerColor = Color(0xFFDCDCDC),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        )
    )
}

@Composable
fun FilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        filters.forEach { filter ->

            val isSelected = filter == selectedFilter

            Surface(
                modifier = Modifier
                    .weight(0.5f)
                    .width(6.dp)
                    .height(26.dp)
                    .clickable {
                        onFilterSelected(filter)
                    },
                shape = RoundedCornerShape(15.dp),
                color = if (isSelected) Color.Black else Color(0xFFDCDCDC)
            ) {

                Box(
                    contentAlignment = Alignment.Center,

                    ) {

                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        if (subtitle.isNotEmpty()) {

            Text(
                text = subtitle,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TaskItemView(
    task: TaskItem
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFEDEDED),
        border = BorderStroke(
            1.dp,
            Color(0xFFDADADA)
        )
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(
                        1.dp,
                        Color(0xFFBDBDBD),
                        RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = task.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = task.tagBgColor
                    ) {

                        Text(
                            text = task.tag,
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ),
                            fontSize = 8.sp,
                            color = task.tagTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.timer),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Black
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = task.time,
                        fontSize = 9.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.baseline_more_vert_24),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun PriorityTaskCard() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE9E9E9)
    ) {

        Box(modifier = Modifier.padding(16.dp)) {

            Column {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                ) {

                    Text(
                        text = "HIGH PRIORITY",
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        ),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Final Exam Review:\nNeuroscience",
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Focus on synaptic plasticity and memory\nformation modules.",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Grouped profile icons placeholder
                    Row {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                        Box(
                            modifier = Modifier
                                .offset(x = (-6).dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.DarkGray)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "4:00 PM Submission",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            // Checkbox and Illustration on the right
            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End
            ) {

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .border(
                            1.dp,
                            Color(0xFFBDBDBD),
                            RoundedCornerShape(4.dp)
                        )
                )


                Image(
                    painter = painterResource(id = R.drawable.review),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
fun OverdueMilestoneCard() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE9E9E9)
    ) {

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "!",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Overdue Milestone",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Organic Chemistry Lab\nReport-2 days past\ndue.",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.overdue),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "RESOLVE\nNOW",
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun TaskFAB(
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.size(48.dp),
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        border = BorderStroke(
            3.dp,
            Color.Black
        )
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add",
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun TaskBottomNavigation() {

    val context = LocalContext.current

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(72.dp)
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = label,
                            fontSize = 8.sp
                        )
                    }
                },
                selected = isSelected,
                onClick = {

                    when (label) {

                        "Exams" -> {
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                        }

                        "Home" -> {
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                        }

                        "Timer" -> {
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}