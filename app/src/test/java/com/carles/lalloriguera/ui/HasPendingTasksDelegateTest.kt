package com.carles.lalloriguera.ui

import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.ui.viewmodel.HasPendingTasksDelegate
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class HasPendingTasksDelegateTest {

    private val pendingTask: Tasc = mockk()
    private val onTimeTask: Tasc = mockk()
    private val onTimeTask2: Tasc = mockk()
    private lateinit var delegate: HasPendingTasksDelegate

    @Before
    fun setup() {
        delegate = HasPendingTasksDelegate()
        every { pendingTask.daysRemaining } returns -1
        every { onTimeTask.daysRemaining } returns 0
        every { onTimeTask2.daysRemaining } returns 1
    }

    @Test
    fun `given hasPendingTasks, when there are no pending tasks, then return false`() {
        assertFalse(delegate.hasPendingTasks(listOf(onTimeTask, onTimeTask2)))
    }

    @Test
    fun `given hasPendingTasks, when there are any pending task, then return true`() {
        assertTrue(delegate.hasPendingTasks(listOf(onTimeTask, pendingTask, onTimeTask2)))
    }

    @Test
    fun `given hasPendingTasks, when tasks are empty, then return false`() {
        assertFalse(delegate.hasPendingTasks(emptyList()))
    }
}