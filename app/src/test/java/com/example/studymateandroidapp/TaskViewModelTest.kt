package com.example.studymateandroidapp

import android.app.Application
import com.example.studymateandroidapp.data.model.Priority
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.model.TaskStatus
import com.example.studymateandroidapp.data.repository.MotivationRepository
import com.example.studymateandroidapp.data.repository.TaskRepository
import com.example.studymateandroidapp.utils.notification.ReminderScheduler
import com.example.studymateandroidapp.viewmodel.TaskViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewmodel
    private val repository: TaskRepository = mock()
    private val motivationRepository: MotivationRepository = mock()
    private val reminderScheduler: ReminderScheduler = mock()
    private val application: Application = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(repository.allTasks).thenReturn(tasksFlow)
        viewModel = TaskViewmodel(repository, motivationRepository, reminderScheduler, application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `allTasks flow sorts tasks by isPinned`() = runTest {
        val task1 = Task(id = 1, title = "Task 1", isPinned = false)
        val task2 = Task(id = 2, title = "Task 2", isPinned = true)
        
        tasksFlow.value = listOf(task1, task2)
        
        advanceUntilIdle()
        
        val result = viewModel.allTasks.value
        assertEquals(2, result.size)
        assertEquals(2L, result[0].id)
        assertEquals(1L, result[1].id)
    }

    @Test
    fun `addTask inserts task and schedules reminders if due date exists`() = runTest {
        val dueDate = LocalDate.now().plusDays(1)
        val dueTime = LocalTime.of(10, 0)
        val task = Task(id = 1, title = "New Task", dueDate = dueDate, dueTime = dueTime)
        
        whenever(repository.insert(any())).thenReturn(1L)

        viewModel.addTask(task)
        advanceUntilIdle()

        verify(repository).insert(task)
        verify(reminderScheduler).scheduleTaskReminders(
            taskId = 1L,
            title = "New Task",
            dueDate = dueDate,
            dueTime = dueTime
        )
    }

    @Test
    fun `updateTask awards XP when task is completed for the first time`() = runTest {
        val task = Task(id = 1, title = "Test Task", isCompleted = true, isXpAwarded = false)
        
        viewModel.updateTask(task)
        advanceUntilIdle()

        verify(motivationRepository).addXp(org.mockito.kotlin.eq(5), any())
        verify(motivationRepository).triggerCelebration(any())
        
        val taskCaptor = argumentCaptor<Task>()
        verify(repository).update(taskCaptor.capture())
        assertEquals(true, taskCaptor.firstValue.isXpAwarded)
        verify(motivationRepository).recordStudyActivity()
    }

    @Test
    fun `updateTask cancels reminders when due date is removed`() = runTest {
        val task = Task(id = 1, title = "Test Task", dueDate = null, dueTime = null)
        
        viewModel.updateTask(task)
        advanceUntilIdle()

        verify(repository).update(task)
        verify(reminderScheduler).cancelTaskReminders(1L)
    }

    @Test
    fun `deleteTask deletes task and cancels reminders`() = runTest {
        val task = Task(id = 1, title = "Delete Me")
        
        viewModel.deleteTask(task)
        advanceUntilIdle()

        verify(repository).delete(task)
        verify(reminderScheduler).cancelTaskReminders(1L)
    }

    @Test
    fun `pinTask calls repository and updates widgets`() = runTest {
        viewModel.pinTask(1L)
        advanceUntilIdle()

        verify(repository).pinTask(1L)
        // WidgetUpdateHelper is hard to verify without mockStatic, 
        // but we ensure the code reaches this point.
    }

    @Test
    fun `togglePinned calls repository`() = runTest {
        viewModel.togglePinned(1L)
        advanceUntilIdle()

        verify(repository).togglePinned(1L)
    }
}
