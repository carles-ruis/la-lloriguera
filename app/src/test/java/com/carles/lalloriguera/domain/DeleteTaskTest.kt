package com.carles.lalloriguera.domain

import MainDispatcherRule
import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeleteTaskTest {

    @get:Rule
    var rule = MainDispatcherRule()

    private val dispatcher = Dispatchers.Main
    private val dispatchers: AppDispatchers = AppDispatchers(dispatcher, dispatcher, dispatcher)

    private val repository: TaskRepository = mockk()
    private lateinit var usecase: DeleteTask

    @Before
    fun setup() {
        usecase = DeleteTask(repository, dispatchers)
    }

    @Test
    fun `given usecase, when task id is provided, then delete task through repository`() = runTest {
        coEvery { repository.deleteTask(any()) } just Runs
        usecase.execute(taskId)
        coVerify { repository.deleteTask(taskId) }
    }

    companion object {
        private const val taskId = "1"
    }

}