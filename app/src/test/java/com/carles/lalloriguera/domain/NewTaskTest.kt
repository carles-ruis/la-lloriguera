package com.carles.lalloriguera.domain

import com.carles.lalloriguera.MainDispatcherRule
import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import com.carles.lalloriguera.model.Tasc
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

class NewTaskTest {

    @get:Rule
    var rule = MainDispatcherRule()

    private val dispatcher = Dispatchers.Main
    private val dispatchers = AppDispatchers(dispatcher, dispatcher, dispatcher)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: NewTask

    @Before
    fun setup() {
        usecase = NewTask(repository, dispatchers)
    }

    @Test
    fun `given usecase, when task is provided, then save task in repository`() = runTest {
        coEvery { repository.saveTask(any()) } just Runs
        usecase.execute(task)
        coVerify { repository.saveTask(task)}
    }

    companion object {
        private val task = Tasc("1", "TASK!", true, 0L, 7, false)
    }
}