package com.example.studymateandroidapp.data.repository

import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.model.Task
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class TaskRepositoryTest {

    @Mock
    private lateinit var taskDao: TaskDao

    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = TaskRepository(taskDao)
    }

    @Test
    fun `getAllTasks returns flow from DAO`() = runTest {
        val mockTasks = listOf(Task(id = 1, title = "Test Task"))
        `when`(taskDao.getAllTasks()).thenReturn(flowOf(mockTasks))

        repository.allTasks.collect { tasks ->
            assertEquals(1, tasks.size)
            assertEquals("Test Task", tasks[0].title)
        }
    }

    @Test
    fun `pinTask calls DAO pinTask`() = runTest {
        repository.pinTask(1L)
        verify(taskDao).pinTask(1L)
    }

    @Test
    fun `unpinTask calls DAO unpinTask`() = runTest {
        repository.unpinTask(1L)
        verify(taskDao).unpinTask(1L)
    }

    @Test
    fun `sorting pinned tasks first preserves relative order`() {
        val t1 = Task(id = 1, title = "Task 1", isPinned = false)
        val t2 = Task(id = 2, title = "Task 2", isPinned = true)
        val t3 = Task(id = 3, title = "Task 3", isPinned = false)
        val t4 = Task(id = 4, title = "Task 4", isPinned = true)

        val list = listOf(t1, t2, t3, t4)
        val sorted = list.sortedByDescending { it.isPinned }

        assertEquals(4, sorted.size)
        // Pinned tasks first
        assertEquals(t2, sorted[0])
        assertEquals(t4, sorted[1])
        // Unpinned tasks next
        assertEquals(t1, sorted[2])
        assertEquals(t3, sorted[3])
    }
}
