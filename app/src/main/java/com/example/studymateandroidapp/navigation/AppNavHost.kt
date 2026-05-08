package com.example.studymateandroidapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studymateandroidapp.feature.calendar.ui.CalendarScreen
import com.example.studymateandroidapp.feature.calendar.viewmodel.CalendarViewModel
import com.example.studymateandroidapp.feature.dashboard.ui.DashboardScreen
import com.example.studymateandroidapp.feature.dashboard.viewmodel.DashboardViewModel
import com.example.studymateandroidapp.feature.exams.ui.AddEditExamScreen
import com.example.studymateandroidapp.feature.exams.ui.ExamDetailRoute
import com.example.studymateandroidapp.feature.exams.ui.ExamDetailScreen
import com.example.studymateandroidapp.feature.exams.ui.ExamsScreen
import com.example.studymateandroidapp.feature.exams.viewmodel.AddEditExamViewModel
import com.example.studymateandroidapp.feature.exams.viewmodel.ExamViewModel
import com.example.studymateandroidapp.feature.goals.ui.AddGoalScreen
import com.example.studymateandroidapp.feature.goals.ui.GoalsScreen
import com.example.studymateandroidapp.feature.goals.viewmodel.GoalViewModel
import com.example.studymateandroidapp.feature.sessions.ui.StudyTimerScreen
import com.example.studymateandroidapp.feature.sessions.viewmodel.SessionViewModel
import com.example.studymateandroidapp.feature.settings.ui.SettingsScreen
import com.example.studymateandroidapp.feature.settings.ui.EditProfileScreen
import com.example.studymateandroidapp.feature.settings.ui.SettingsViewModel
import com.example.studymateandroidapp.feature.flashcards.ui.FlashcardStudyScreen
import com.example.studymateandroidapp.feature.flashcards.ui.FlashcardListScreen
import com.example.studymateandroidapp.feature.flashcards.ui.AddEditFlashcardScreen
import com.example.studymateandroidapp.feature.flashcards.viewmodel.FlashcardViewModel
import com.example.studymateandroidapp.feature.notes.ui.NotesScreen
import com.example.studymateandroidapp.feature.notes.ui.AddEditNoteScreen
import com.example.studymateandroidapp.feature.notes.viewmodel.NoteViewModel
import com.example.studymateandroidapp.feature.statistics.ui.StatisticsScreen
import com.example.studymateandroidapp.feature.statistics.viewmodel.StatisticsViewModel
import com.example.studymateandroidapp.feature.tasks.ui.AddEditTaskScreen
import com.example.studymateandroidapp.feature.tasks.ui.TaskListScreen
import com.example.studymateandroidapp.feature.tasks.viewmodel.AddEditTaskViewModel
import com.example.studymateandroidapp.feature.tasks.viewmodel.TaskListViewModel
import com.example.studymateandroidapp.feature.motivation.ui.DailyReflectionScreen
import com.example.studymateandroidapp.feature.motivation.ui.AchievementsScreen
import com.example.studymateandroidapp.feature.motivation.viewmodel.MotivationViewModel
import com.example.studymateandroidapp.ui.ViewModelFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
            val vm: TaskListViewModel = viewModel(factory = ViewModelFactory)
            TaskListScreen(
                viewModel = vm,
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }

        // ── Add Task ──────────────────────────────────
        composable(Screen.AddTask.route) {
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
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm: SessionViewModel = viewModel(factory = ViewModelFactory)

            LaunchedEffect(examId) {
                if (examId != null) {
                    vm.onExamIdChanged(examId)
                }
            }

            StudyTimerScreen(viewModel = vm)
        }

        // ── Statistics ────────────────────────────────
        composable(Screen.Statistics.route) {
            val vm: StatisticsViewModel = viewModel(factory = ViewModelFactory)
            StatisticsScreen(viewModel = vm)
        }

        // ── Exams ─────────────────────────────────────
        composable(Screen.Exams.route) {
            val vm: ExamViewModel = viewModel(factory = ViewModelFactory)
            ExamsScreen(
                viewModel = vm,
                onNavigateToAddExam = { navController.navigate(Screen.AddExam.route) },
                onNavigateToEditExam = { examId ->
                    navController.navigate(Screen.EditExam.createRoute(examId))
                },
                onStartStudy = { examId ->
                    navController.navigate(Screen.StudyTimer.createRoute(examId))
                },
                onNavigateToNotes = { examId ->
                    navController.navigate(Screen.Notes.createRoute(examId))
                },
                onNavigateToFlashcards = { examId ->
                    navController.navigate(Screen.Flashcards.createRoute(examId))
                },
                onNavigateToExamDetail = { examId ->
                    navController.navigate(Screen.ExamDetail.createRoute(examId))
                }
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

        composable(Screen.AddExam.route) {
            val vm: AddEditExamViewModel = viewModel(factory = ViewModelFactory)
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
        }

        // ── Goals ─────────────────────────────────────
        composable(Screen.Goals.route) {
            val vm: GoalViewModel = viewModel(factory = ViewModelFactory)
            GoalsScreen(
                viewModel = vm,
                onNavigateToAddGoal = { navController.navigate(Screen.AddGoal.route) }
            )
        }

        composable(Screen.AddGoal.route) {
            val vm: GoalViewModel = viewModel(factory = ViewModelFactory)
            AddGoalScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Settings ──────────────────────────────────
        composable(Screen.Settings.route) {
            val vm: SettingsViewModel = viewModel(factory = ViewModelFactory)
            SettingsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        composable(Screen.EditProfile.route) {
            val vm: SettingsViewModel = viewModel(factory = ViewModelFactory)
            EditProfileScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Notes ─────────────────────────────────────
        composable(
            route = Screen.Notes.route + "?examId={examId}",
            arguments = listOf(navArgument("examId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm: NoteViewModel = viewModel(factory = ViewModelFactory)

            LaunchedEffect(examId) {
                vm.setExamFilter(examId)
            }

            NotesScreen(
                viewModel = vm,
                onNavigateToAddEdit = { noteId ->
                    navController.navigate(Screen.AddNote.createRoute(noteId, examId))
                }
            )
        }

        composable(
            route = Screen.AddNote.route,
            arguments = listOf(
                navArgument("noteId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("examId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")?.takeIf { it != -1L }
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }

            val vm: NoteViewModel = viewModel(factory = ViewModelFactory)
            AddEditNoteScreen(
                noteId = noteId,
                examId = examId,
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }        // ── Flashcard List (Deck Management) ──────────
        composable(
            route = Screen.Flashcards.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId")?.takeIf { it != -1L }
            val vm: FlashcardViewModel = viewModel(factory = ViewModelFactory)

            FlashcardListScreen(
                viewModel = vm,
                examId = examId,
                onStartStudyAll = {
                    navController.navigate(Screen.StudyFlashcards.createRoute(-1L))
                },
                onStartStudyExam = { id ->
                    navController.navigate(Screen.StudyFlashcards.createRoute(id))
                },
                onAddCard = {
                    navController.navigate(Screen.AddFlashcard.createRoute(examId = examId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Exam-linked Flashcards ────────────────────
        composable(
            route = Screen.StudyFlashcards.route,
            arguments = listOf(navArgument("examId") { type = NavType.LongType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            val vm: FlashcardViewModel =
                viewModel(viewModelStoreOwner = backStackEntry, factory = ViewModelFactory)
            FlashcardStudyScreen(
                examId = examId,
                examTitle = "Study Session",
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
            val vm: FlashcardViewModel = viewModel(factory = ViewModelFactory)
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
