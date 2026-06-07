package com.example.studymateandroidapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studymateandroidapp.ui.ViewModelFactory
import com.example.studymateandroidapp.ui.screens.*
import com.example.studymateandroidapp.viewmodel.*

/**
 * Central navigation host binding routes to screen composables.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sharedTimerVm: TimerViewmodel = viewModel(factory = ViewModelFactory)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // ── Splash ────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Dashboard ─────────────────────────────────
        composable(Screen.Dashboard.route) {
            val vm: DashboardViewModel = viewModel(factory = ViewModelFactory)
            DashboardScreen(
                viewModel = vm,
                onNavigateToTasks = { 
                    navController.navigate(Screen.Tasks.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToTimer = { 
                    navController.navigate("study_timer") {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToExams = { 
                    navController.navigate("exams") {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGoals = { navController.navigate(Screen.Goals.route) },
                onNavigateToSettings = { 
                    navController.navigate("settings") {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToReflection = { navController.navigate(Screen.DailyReflection.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) }
            )
        }

        // ── Task List ─────────────────────────────────
        composable(Screen.Tasks.route) {
            val vm: TaskViewmodel = viewModel(factory = ViewModelFactory)
            TaskScreen(
                viewModel = vm,
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }

        // ── Add/Edit Task ─────────────────────────────
        composable(Screen.AddTask.route) {
            val vm: TaskViewmodel = viewModel(factory = ViewModelFactory)
            AddEditTaskScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            val vm: TaskViewmodel = viewModel(factory = ViewModelFactory)
            AddEditTaskScreen(
                taskId = taskId,
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

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
            val examIdArg = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            
            androidx.compose.runtime.LaunchedEffect(examIdArg) {
                sharedTimerVm.setExamId(examIdArg)
            }
            
            TimerScreen(viewModel = sharedTimerVm, examId = examIdArg)
        }

        // ── Statistics ────────────────────────────────
        composable(Screen.Statistics.route) {
            val vm: StatisticsViewmodel = viewModel(factory = ViewModelFactory)
            StatisticsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }

        // ── Exams ─────────────────────────────────────
        composable(Screen.Exams.route) {
            val vm: ExamViewmodel = viewModel(factory = ViewModelFactory)
            ExamScreen(
                viewModel = vm,
                onNavigateToAddExam = { navController.navigate(Screen.AddExam.route) },
                onNavigateToEditExam = { examId ->
                    navController.navigate(Screen.ExamDetail.createRoute(examId))
                },
                onStartStudy = { examId ->
                    navController.navigate(Screen.StudyTimer.createRoute(examId)) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToNotes = { examId ->
                    navController.navigate(Screen.Notes.createRoute(examId))
                },
                onNavigateToFlashcards = { examId ->
                    navController.navigate(Screen.Flashcards.createRoute(examId))
                }
            )
        }

        composable(
            route = Screen.ExamDetail.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            val vm: ExamViewmodel = viewModel(factory = ViewModelFactory)
            ExamDetailScreen(
                examId = examId,
                viewModel = vm,
                onNavigateToEdit = { id -> navController.navigate(Screen.EditExam.createRoute(id)) },
                onNavigateToNotes = { id -> navController.navigate(Screen.Notes.createRoute(id)) },
                onNavigateToFlashcards = { id -> navController.navigate(Screen.Flashcards.createRoute(id)) },
                onStartStudy = { id -> 
                    navController.navigate(Screen.StudyTimer.createRoute(id)) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddExam.route) {
            val vm: ExamViewmodel = viewModel(factory = ViewModelFactory)
            AddEditExamScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditExam.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            val vm: ExamViewmodel = viewModel(factory = ViewModelFactory)
            AddEditExamScreen(
                examId = examId,
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Calendar ──────────────────────────────────
        composable(Screen.Calendar.route) {
            val vm: CalendarViewModel = viewModel(factory = ViewModelFactory)
            CalendarScreen(
                viewModel = vm,
                onNavigateToTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                },
                onNavigateToExam = { examId ->
                    navController.navigate(Screen.EditExam.createRoute(examId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Goals ─────────────────────────────────────
        composable(Screen.Goals.route) {
            val vm: GoalViewmodel = viewModel(factory = ViewModelFactory)
            GoalScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Settings ──────────────────────────────────
        composable(Screen.Settings.route) {
            val vm: SettingViewmodel = viewModel(factory = ViewModelFactory)
            SettingsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        composable(Screen.EditProfile.route) {
            val vm: SettingViewmodel = viewModel(factory = ViewModelFactory)
            EditProfileScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Notes ─────────────────────────────────────
        composable(
            route = Screen.Notes.route,
            arguments = listOf(navArgument("examId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm: NoteViewmodel = viewModel(factory = ViewModelFactory)
            NoteScreen(
                examId = examId,
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Flashcards ────────────────────────────────
        composable(
            route = Screen.Flashcards.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm: FlashcardViewmodel = viewModel(factory = ViewModelFactory)
            FlashcardScreen(
                viewModel = vm,
                examId = examId,
                onStartStudy = { id ->
                    navController.navigate(Screen.StudyFlashcards.createRoute(id))
                },
                onAddCard = {
                    navController.navigate(Screen.AddFlashcard.createRoute(examId = examId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StudyFlashcards.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            val vm: FlashcardViewmodel = viewModel(factory = ViewModelFactory)
            FlashcardStudyScreen(
                examId = examId,
                viewModel = vm,
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
            val vm: FlashcardViewmodel = viewModel(factory = ViewModelFactory)
            AddEditFlashcardScreen(
                examId = examId ?: -1L,
                cardId = cardId,
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Daily Reflection ──────────────────────────
        composable(Screen.DailyReflection.route) {
            val vm: MotivationViewModel = viewModel(factory = ViewModelFactory)
            DailyReflectionScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
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
