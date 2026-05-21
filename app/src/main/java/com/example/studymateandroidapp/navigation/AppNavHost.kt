package com.example.studymateandroidapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studymateandroidapp.feature.achievements.ui.AchievementsScreen
import com.example.studymateandroidapp.feature.calendar.ui.CalendarScreen
import com.example.studymateandroidapp.feature.calendar.viewmodel.CalendarViewModel
import com.example.studymateandroidapp.feature.dashboard.ui.DashboardScreen
import com.example.studymateandroidapp.feature.dashboard.viewmodel.DashboardViewModel
import com.example.studymateandroidapp.feature.exam.ui.AddEditExamScreen
import com.example.studymateandroidapp.feature.exam.ui.ExamScreen
import com.example.studymateandroidapp.feature.exam.viewmodel.ExamViewmodel
import com.example.studymateandroidapp.feature.timer.ui.TimerScreen
import com.example.studymateandroidapp.feature.timer.viewmodel.TimerViewmodel
import com.example.studymateandroidapp.feature.settings.ui.EditProfileScreen
import com.example.studymateandroidapp.feature.settings.viewmodel.SettingViewmodel
import com.example.studymateandroidapp.feature.flashcard.ui.FlashcardScreen
import com.example.studymateandroidapp.feature.flashcard.ui.AddEditFlashcardScreen
import com.example.studymateandroidapp.feature.flashcard.ui.FlashcardStudyScreen
import com.example.studymateandroidapp.feature.flashcard.viewmodel.FlashcardViewmodel
import com.example.studymateandroidapp.feature.statistics.viewmodel.StatisticsViewmodel
import com.example.studymateandroidapp.feature.task.ui.TaskScreen
import com.example.studymateandroidapp.feature.task.viewmodel.TaskViewmodel
import com.example.studymateandroidapp.feature.goal.ui.GoalScreen
import com.example.studymateandroidapp.feature.goal.viewmodel.GoalViewmodel
import com.example.studymateandroidapp.feature.note.ui.NoteScreen
import com.example.studymateandroidapp.feature.note.viewmodel.NoteViewmodel
import com.example.studymateandroidapp.feature.motivation.viewmodel.MotivationViewModel
import com.example.studymateandroidapp.feature.reflection.ui.DailyReflectionScreen
import com.example.studymateandroidapp.feature.reflection.ui.ReflectionHistoryScreen
import com.example.studymateandroidapp.ui.ViewModelFactory
import com.example.studymateandroidapp.feature.settings.ui.SettingsScreen
import com.example.studymateandroidapp.feature.statistics.ui.StatisticsScreen

/**
 * Central navigation host binding routes to screen composables.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        // ── Dashboard ─────────────────────────────────
        composable(Screen.Dashboard.route) {
            val vm: DashboardViewModel = viewModel(factory = ViewModelFactory)
            DashboardScreen(
                viewModel = vm,
                onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateToTimer = { navController.navigate(Screen.StudyTimer.route) },
                onNavigateToExams = { navController.navigate(Screen.Exams.route) },
                onNavigateToGoals = { navController.navigate(Screen.Goals.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToReflection = { navController.navigate(Screen.DailyReflection.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) }
            )
        }

        // ── Task List ─────────────────────────────────
        composable(Screen.Tasks.route) {
            val vm = viewModel<TaskViewmodel>(factory = ViewModelFactory)
            TaskScreen()
        }

        // ── Add Task ──────────────────────────────────
        /*composable(Screen.AddTask.route) {
            val vm: AddEditTaskViewModel = viewModel(factory = ViewModelFactory)
            AddEditTaskScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Edit Task ─────────────────────────────────
        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            val vm: AddEditTaskViewModel = viewModel(factory = ViewModelFactory)
            vm.loadTask(taskId)
            AddEditTaskScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }*/

        // ── Study Timer ───────────────────────────────
        composable(
            route = Screen.StudyTimer.route,
            arguments = listOf(
                navArgument("examId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm = viewModel<TimerViewmodel>(factory = ViewModelFactory)

            /*LaunchedEffect(examId) {
                if (examId != null) {
                    vm.onExamIdChanged(examId)
                }
            }*/

            TimerScreen()
        }

        // ── Statistics ────────────────────────────────
        composable(Screen.Statistics.route) {
            val vm = viewModel<StatisticsViewmodel>(factory = ViewModelFactory)
            StatisticsScreen()
        }

        // ── Exams ─────────────────────────────────────
        composable(Screen.Exams.route) {
            val vm = viewModel<ExamViewmodel>(factory = ViewModelFactory)
            ExamScreen()
        }

        // ── Calendar ──────────────────────────────────
        composable(Screen.Calendar.route) {
            val vm: CalendarViewModel = viewModel(factory = ViewModelFactory)
            CalendarScreen(
                viewModel = vm,
                onNavigateToTask = { taskId ->
                    // navController.navigate(Screen.EditTask.createRoute(taskId))
                },
                onNavigateToExam = { examId ->
                    // navController.navigate(Screen.EditExam.createRoute(examId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddExam.route) {
            // val vm: AddEditExamViewModel = viewModel(factory = ViewModelFactory)
            AddEditExamScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        /*composable(
            route = Screen.EditExam.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            val vm: AddEditExamViewModel = viewModel(factory = ViewModelFactory)
            vm.loadExam(examId)
            AddEditExamScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ExamDetail.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            // CRITICAL: Pass backStackEntry so viewModel() scopes the ViewModel
            // to this destination and SavedStateHandle receives examId correctly.
            ExamDetailRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStudy = { examId ->
                    navController.navigate(Screen.StudyFlashcards.createRoute(examId))
                }
            )
        }*/

        // ── Goals ─────────────────────────────────────
        composable(Screen.Goals.route) {
            val vm = viewModel<GoalViewmodel>(factory = ViewModelFactory)
            GoalScreen()
        }

        // ── Settings ──────────────────────────────────
        composable(Screen.Settings.route) {
            val vm = viewModel<SettingViewmodel>(factory = ViewModelFactory)
            SettingsScreen()
        }

        composable(Screen.EditProfile.route) {
            val vm = viewModel<SettingViewmodel>(factory = ViewModelFactory)
            EditProfileScreen()
        }

        // ── Notes ─────────────────────────────────────
        composable(Screen.Notes.route) {
            val vm = viewModel<NoteViewmodel>(factory = ViewModelFactory)
            NoteScreen()
        }        // ── Flashcard List (Deck Management) ──────────
        composable(
            route = Screen.Flashcards.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm = viewModel<FlashcardViewmodel>(factory = ViewModelFactory)

            FlashcardScreen()
        }

        // ── Exam-linked Flashcards ────────────────────
        composable(
            route = Screen.StudyFlashcards.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            val vm = viewModel<FlashcardViewmodel>(
                viewModelStoreOwner = backStackEntry,
                factory = ViewModelFactory
            )
            FlashcardStudyScreen (
                examId = examId,
                examTitle = "Study Session",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddFlashcard.route,
            arguments = listOf(
                navArgument("cardId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("examId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId")?.takeIf { it != -1L }
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm = viewModel<FlashcardViewmodel>(factory = ViewModelFactory)
            AddEditFlashcardScreen(
                examId = examId ?: -1L,
                cardId = cardId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Daily Reflection ──────────────────────────
        composable(Screen.DailyReflection.route) {
            val vm: MotivationViewModel = viewModel(factory = ViewModelFactory)
            DailyReflectionScreen(navController)
        }

        composable("reflection_history") {
            ReflectionHistoryScreen(navController)
        }

        // ── Achievements ──────────────────────────────
        composable(Screen.Achievements.route) {
            val vm: MotivationViewModel = viewModel(factory = ViewModelFactory)
            AchievementsScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}