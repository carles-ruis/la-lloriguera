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
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MarkTaskAsDoneTest {

    @get:Rule
    var rule = MainDispatcherRule()

    private val dispatcher = Dispatchers.Main
    private val dispatchers = AppDispatchers(dispatcher, dispatcher, dispatcher)
    private val repository: TaskRepository = mockk()
    private lateinit var usecase: MarkTaskAsDone

    @Before
    fun setup() {
        usecase = MarkTaskAsDone(repository, dispatchers)
    }

    @Test
    fun `given usecase, when task is one time, then delete the task`()  = runTest {
        coEvery { repository.deleteTask(any()) } just Runs
        usecase.execute(oneTimeTask)
        coVerify { repository.deleteTask(oneTimeTask.id!!) }
    }

    @Test
    fun `given usecase is executed, when task is periodic, then update its last date`() = runTest {
        val slot = slot<Tasc>()

        coEvery { repository.updateTask(any()) } just Runs
        usecase.execute(periodicTask)
        coVerify { repository.updateTask(capture(slot)) }

        val updatedTask = slot.captured
        assertEquals(updatedTask.id, periodicTask.id)
        assertEquals(updatedTask.name, periodicTask.name)
        assertEquals(updatedTask.periodicity, periodicTask.periodicity)
        assertEquals(updatedTask.isOneTime, periodicTask.isOneTime)
    }

    companion object {
        private val oneTimeTask = Tasc("1", "TASK!", true, 0L, 7, false)
        private val periodicTask = Tasc("2", "TASK!!!", false, 0L, 7, false)
    }

}