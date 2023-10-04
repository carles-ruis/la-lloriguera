package com.carles.lalloriguera.ui.viewmodel

import com.carles.lalloriguera.R
import com.carles.lalloriguera.common.TimeHelper
import com.carles.lalloriguera.data.remote.NoConnectionCancellationException
import com.carles.lalloriguera.model.Tasc
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TaskFormDelegateTest {

    private val saveAction: suspend (Tasc) -> Unit = mockk()
    private lateinit var delegate: TaskFormDelegate

    private val taskFromState: Tasc
        get() = (delegate.state.value as TaskFormState.Filling).task

    @Before
    fun setup() {
        delegate = TaskFormDelegate()
        mockkObject(TimeHelper.Companion)
    }

    @Test
    fun `given setLoadingState, when called, then set state to ShowProgress`() {
        delegate.setLoadingState()
        assertEquals(TaskFormState.ShowProgress, delegate.state.value)
    }

    @Test
    fun `given sendShowErrorEvent, when called, then send ShowError event`() = runTest {
        val message = R.string.no_internet_connection
        delegate.sendShowErrorEvent(message, false)
        assertEquals(TaskFormEvent.ShowError(message, false), delegate.event.first())
    }

    @Test
    fun `given sendDeletedEvent, when called, then send Deleted event`() = runTest {
        delegate.initTask(task)
        delegate.sendDeletedEvent()
        assertEquals(TaskFormEvent.Deleted(task.name), delegate.event.first())
    }

    @Test
    fun `given onNameChange, when name is passed, then update task with this name and set isValid to true`() {
        val newName = "new task name"
        delegate.initTask(task)
        delegate.onNameChange(newName)
        val state = delegate.state.value as TaskFormState.Filling
        assertEquals(task.copy(name = newName), state.task)
        assertTrue(state.isValid)
    }

    @Test
    fun `given onOneTimeChange, when isOneTime is passed, then update task with new value and update lastDate and periodicity`() {
        delegate.initTask(task)
        var previousDate = task.lastDate

        delegate.onOneTimeChange(true)
        assertTrue(taskFromState.isOneTime)
        assertTrue(taskFromState.lastDate > previousDate)
        assertEquals(DEFAULT_PERIODICITY, taskFromState.periodicity)

        previousDate = task.lastDate
        delegate.onOneTimeChange(false)
        assertFalse(taskFromState.isOneTime)
        assertTrue(taskFromState.lastDate > previousDate)
    }

    @Test
    fun `given onLastDateChange, when lastDate is passed, then update lastDate in the task`() {
        val newDate = 1_000L
        delegate.initTask(task)
        delegate.onOneTimeChange(false)
        delegate.onLastDateChange(newDate)
        assertEquals(newDate, taskFromState.lastDate)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given onLastDateChange, when task is one time, then throw exception`() {
        delegate.initTask(task)
        delegate.onOneTimeChange(true)
        delegate.onLastDateChange(1_000L)
    }

    @Test
    fun `given onNextDateChange, when nextDate is passed, then update lastDate and periodicity`() {
        val expectedPeriodicity = 10
        every { TimeHelper.getDaysBetweenDates(any(), any()) } returns expectedPeriodicity

        delegate.initTask(task)
        delegate.onOneTimeChange(true)
        val previousDate = task.lastDate
        val nextDate = 2_000L

        delegate.onNextDateChange(nextDate)
        verify { TimeHelper.getDaysBetweenDates(any(), nextDate) }
        assertTrue(taskFromState.lastDate > previousDate)
        assertEquals(expectedPeriodicity, taskFromState.periodicity)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given onNextDateChange, when isOneTime is false, then throw exception`() {
        delegate.initTask(task)
        delegate.onOneTimeChange(false)
        delegate.onNextDateChange(2_000L)
    }

    @Test
    fun `given onPeriodicityChange, when periodicity is passed, then update task with periodicity`() {
        val periodicity = 9
        delegate.initTask(task)
        delegate.onPeriodicityChange(periodicity)
        assertEquals(periodicity, taskFromState.periodicity)
    }

    @Test
    fun `given onNotificationsChange, when notificationsOn is passed, then update task with notificationsOn value`() {
        delegate.initTask(task)
        delegate.onNotificationsChange(true)
        assertTrue(taskFromState.notificationsOn)
        delegate.onNotificationsChange(false)
        assertFalse(taskFromState.notificationsOn)
    }

    @Test
    fun `given onSaveClick, when taskname is empty, then set isValid to false`() = runTest {
        delegate.initTask(noNameTask)
        delegate.onSaveClick(saveAction)
        val state = delegate.state.value as TaskFormState.Filling
        assertFalse(state.isValid)
        assertEquals(noNameTask, state.task)
    }

    @Test
    fun `given onSaveClick, when save action is successful, then send Save event`() = runTest {
        coEvery { saveAction.invoke(any()) } just Runs

        delegate.initTask(task)
        delegate.onSaveClick(saveAction)

        coVerify { saveAction.invoke(task) }
        assertEquals(TaskFormState.ShowProgress, delegate.state.value)
        assertEquals(TaskFormEvent.Saved(task.name), delegate.event.first())
    }

    @Test
    fun `given onSaveClick, when save action returns connection error, then send ShowError event with no connection message`() =
        runTest {
            coEvery { saveAction.invoke(any()) } throws NoConnectionCancellationException()

            delegate.initTask(task)
            delegate.onSaveClick(saveAction)

            coVerify { saveAction.invoke(task) }
            assertEquals(TaskFormState.Filling(task, true), delegate.state.value)
            assertEquals(TaskFormEvent.ShowError(R.string.no_internet_connection), delegate.event.first())
        }

    @Test
    fun `given onSaveClick, when save action returns save error, then send ShowError event with save error message`() =
        runTest {
            coEvery { saveAction.invoke(any()) } throws RuntimeException("some error")

            delegate.initTask(task)
            delegate.onSaveClick(saveAction)

            coVerify { saveAction.invoke(task) }
            assertEquals(TaskFormState.Filling(task, true), delegate.state.value)
            assertEquals(TaskFormEvent.ShowError(R.string.edit_task_save_error), delegate.event.first())
        }

    companion object {
        private const val DEFAULT_PERIODICITY = 7
        private val task = Tasc("1", "the task", false, 0L, 0, false)
        private val noNameTask = task.copy(name = "")
    }
}