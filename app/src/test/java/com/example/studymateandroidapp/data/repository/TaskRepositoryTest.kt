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
}
