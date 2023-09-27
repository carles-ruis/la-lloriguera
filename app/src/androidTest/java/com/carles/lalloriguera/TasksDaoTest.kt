package com.carles.lalloriguera

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.carles.lalloriguera.data.local.TaskDao
import com.carles.lalloriguera.data.local.TaskDatabase
import com.carles.lalloriguera.data.local.TaskEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class TasksDaoTest {

    private lateinit var dao: TaskDao
    private lateinit var database: TaskDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            context = InstrumentationRegistry.getInstrumentation().context,
            klass = TaskDatabase::class.java
        )
            .allowMainThreadQueries().build()
        dao = database.taskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun givenLoadTask_whenCalledWithId_thenReturnTaskWithThatId() = runTest {
        dao.saveTask(task1)
        assertEquals(task1, dao.loadTask(taskId))
    }

    @Test
    fun givenLoadTask_whenTaskIsNotStored_thenReturnTaskWithThatId() = runTest {
        assertNull(dao.loadTask(taskId))
    }

    @Test
    fun givenLoadTasks_whenCalled_thenReturnAllTasks() = runTest {
        val expected = listOf(task1, task2)
        dao.saveTask(task1)
        dao.saveTask(task2)
        assertEquals(expected, dao.loadTasks().first())
    }

    @Test
    fun givenUpdateTask_whenTaskPassed_thenUpdateTaskInDatabase() = runTest {
        val newName = "super task"
        val updatedTask = task1.copy(name = newName)
        dao.saveTask(task1)
        dao.updateTask(updatedTask)
        assertEquals(updatedTask, dao.loadTask(taskId))
    }

    @Test
    fun givenUpdateTask_whenTaskWasNotStored_thenDoNotStoreIt() = runTest {
        dao.updateTask(task1)
        assertNull(dao.loadTask(taskId))
    }

    @Test
    fun givenDeleteTask_whenIdIsPassed_thenDeleteTaskWithThatId() = runTest {
        dao.saveTask(task1)
        dao.deleteTask(taskId)
        assertNull(dao.loadTask(taskId))
    }

    companion object {
        private const val taskId = 1
        private val task1 = TaskEntity(taskId, "the task", false, 0L, 7, false)
        private val task2 = TaskEntity(2, "the task two", true, 0L, 7, false)
    }
}