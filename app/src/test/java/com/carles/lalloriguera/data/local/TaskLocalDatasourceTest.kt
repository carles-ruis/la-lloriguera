package com.carles.lalloriguera.data.local

import com.carles.lalloriguera.data.TaskMapper
import com.carles.lalloriguera.model.Tasc
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TaskLocalDatasourceTest {

    private val mapper: TaskMapper = mockk()
    private val dao: TaskDao = mockk()
    private lateinit var datasource: TaskLocalDatasource

    @Before
    fun setup() {
        datasource = TaskLocalDatasource(mapper, dao)
    }

    @Test
    fun `given getTask, when called passing a task id, return the task with given id`() = runTest {
        coEvery { mapper.fromEntity(any()) } returns task1
        coEvery { dao.loadTask(1) } returns entity1

        val result = datasource.getTask("1")
        coVerify { mapper.fromEntity(entity1) }
        coVerify { dao.loadTask(1) }
        assertEquals(task1, result)
    }

    @Test
    fun `given getTasks, when called, return the list of tasks as a flow`() = runTest {
        val taskList = listOf(task1, task2)
        coEvery { mapper.fromEntity(any()) } returnsMany taskList
        coEvery { dao.loadTasks() } returns flow { emit(listOf(entity1, entity2)) }

        val result = datasource.getTasks().first()
        coVerify { mapper.fromEntity(entity1) }
        coVerify { mapper.fromEntity(entity2) }
        coVerify { dao.loadTasks() }
        assertEquals(result, taskList)
    }

    @Test
    fun `given saveTask, when task is passed, then save it locally`() = runTest {
        coEvery { dao.saveTask(any()) } returns 1L
        coEvery { mapper.toEntity(any()) } returns entity1
        datasource.saveTask(task1)
        coVerify { dao.saveTask(entity1) }
        coVerify { mapper.toEntity(task1)}
    }

    @Test
    fun `given updateTask, when task is passed, then update it locally`() = runTest {
        coEvery { dao.updateTask(any()) } returns 1
        coEvery { mapper.toEntity(any()) } returns entity1
        datasource.updateTask(task1)
        coVerify { dao.updateTask(entity1) }
        coVerify { mapper.toEntity(task1) }
    }

    @Test
    fun `given deleteTask, when task id is passed, then delete task with provided id`() = runTest {
        coEvery { dao.deleteTask(2) } returns 1
        datasource.deleteTask("2")
        coVerify { dao.deleteTask(2) }
    }

    companion object {
        private val entity1 = TaskEntity(1, "task one", false, 0L, 1, false)
        private val entity2 = TaskEntity(2, "task two", false, 0L, 1, false)
        private val task1 = Tasc("1", "task one", false, 0L, 1, false)
        private val task2 = Tasc("2", "task two", false, 0L, 1, false)
    }

}


