package com.carles.lalloriguera.domain

import com.carles.lalloriguera.MainDispatcherRule
import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import com.carles.lalloriguera.model.Tasc
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetTaskTest {

    @get:Rule
    var rule = MainDispatcherRule()

    private val dispatcher = Dispatchers.Main
    private val dispatchers = AppDispatchers(dispatcher, dispatcher, dispatcher)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: GetTask

    @Before
    fun setup() {
        usecase = GetTask(repository, dispatchers)
    }

    @Test
    fun `given usecase, when id is provided, then get task from repository`() = runTest {
        coEvery { repository.getTask(any()) } returns task
        assertEquals(task, usecase.execute(taskId))
        coVerify { repository.getTask(taskId) }
    }

    companion object {
        private const val taskId = "1"
        private val task = Tasc(taskId, "TASK!", true, 0L, 7, false)
    }
}