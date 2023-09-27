package com.carles.lalloriguera.data

import com.carles.lalloriguera.model.Tasc
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    private val datasource: TaskDatasource = mockk()
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        repository = TaskRepository(datasource)
    }

    @Test
    fun `given getTask, when id is provided, then obtain task from datasource`() = runTest {
        coEvery { datasource.getTask(any()) } returns task
        assertEquals(task, repository.getTask(taskId))
        coVerify { datasource.getTask(taskId) }
    }

    @Test
    fun `given getTasks, when called, then obtain tasks from datasource`() = runTest {
        val tasks = listOf(task)
        coEvery { datasource.getTasks() } returns flow { emit(tasks) }
        assertEquals(tasks, repository.getTasks().first())
        coVerify { datasource.getTasks() }
    }

    @Test
    fun `given saveTask, when task is provided, then save it to datasource`() = runTest {
        coEvery { datasource.saveTask(any()) } just Runs
        repository.saveTask(task)
        coVerify { datasource.saveTask(task) }
    }

    @Test
    fun `given updateTask, when task is provided, then update it on datasource`() = runTest {
        coEvery { datasource.updateTask(any()) } just Runs
        repository.updateTask(task)
        coVerify { datasource.updateTask(task) }
    }

    @Test
    fun `given deleteTask, when id is provided, then delete task with given id`() = runTest {
        coEvery { datasource.deleteTask(any()) } just Runs
        repository.deleteTask(taskId)
        coVerify { datasource.deleteTask(taskId) }
    }

    companion object {
        private val taskId = "1"
        private val task = Tasc(taskId, "TASK!", true, 0L, 7, false)
    }


}