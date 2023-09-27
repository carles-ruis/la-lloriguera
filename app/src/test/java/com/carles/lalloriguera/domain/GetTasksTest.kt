package com.carles.lalloriguera.domain

import MainDispatcherRule
import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import com.carles.lalloriguera.model.Tasc
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetTasksTest {

    @get:Rule
    var rule = MainDispatcherRule()

    private val dispatcher = Dispatchers.Main
    private val dispatchers = AppDispatchers(dispatcher, dispatcher, dispatcher)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: GetTasks

    @Before
    fun setup() {
        usecase = GetTasks(repository, dispatchers)
    }

    @Test
    fun `given usecase, when executed, then get tasks from repository`() = runTest {
        val tasks = listOf(task)
        coEvery { repository.getTasks() } returns flowOf(listOf(task))
        assertEquals(tasks, usecase.execute().first())
        coVerify { repository.getTasks() }
    }

    companion object {
        private val task = Tasc("1", "TASK!", true, 0L, 7, false)
    }
}