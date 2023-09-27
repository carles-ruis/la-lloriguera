package com.carles.lalloriguera.ui

import MainDispatcherRule
import com.carles.lalloriguera.R
import com.carles.lalloriguera.data.remote.NoConnectionCancellationException
import com.carles.lalloriguera.domain.GetTasks
import com.carles.lalloriguera.domain.MarkTaskAsDone
import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.ui.viewmodel.HasPendingTasksDelegate
import com.carles.lalloriguera.ui.viewmodel.TasksEvent
import com.carles.lalloriguera.ui.viewmodel.TasksState
import com.carles.lalloriguera.ui.viewmodel.TasksViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val getTasks: GetTasks = mockk()
    private val markTaskAsDone: MarkTaskAsDone = mockk()
    private val delegate: HasPendingTasksDelegate = mockk()
    private lateinit var viewModel: TasksViewModel

    private fun initViewModel() {
        coEvery { getTasks.execute() } returns flowOf(tasks)
        viewModel = TasksViewModel(getTasks, markTaskAsDone, delegate)
    }

    @Test
    fun `given initialization, when tasks are obtained with success, then set tasks data state`() {
        initViewModel()
        coVerify { getTasks.execute() }
        assertTrue(viewModel.state.value == TasksState.Data(tasks))
    }

    // @Test
    // how to test catch block ???
    fun `given initialization, when there is a no connection exception, then set error state with no connection message`() {
        coEvery { getTasks.execute() } throws NoConnectionCancellationException()
        viewModel = TasksViewModel(getTasks, markTaskAsDone, delegate)
        coVerify { getTasks.execute() }
        assertTrue(viewModel.state.value == TasksState.Error(R.string.no_internet_connection))
    }

    @Test
    fun `given retry, when called, then get tasks`() {
        initViewModel()
        viewModel.retry()
        coVerify(exactly = 2) { getTasks.execute() }
    }

    @Test
    fun `given onTaskDone, when is marked succesfully, then send task done event`() = runTest {
        initViewModel()
        coEvery { markTaskAsDone.execute(any()) } just Runs
        viewModel.onTaskDone(task1)
        coVerify { markTaskAsDone.execute(task1) }
        assertTrue(viewModel.event.first() == TasksEvent.TaskDone(task1.name))
    }

    @Test
    fun `given onTaskDone, when there is a connection error, then show connection error message`() = runTest {
        initViewModel()
        coEvery { markTaskAsDone.execute(any()) } throws NoConnectionCancellationException()
        viewModel.onTaskDone(task1)
        coVerify { markTaskAsDone.execute(task1) }
        assertTrue(viewModel.event.first() == TasksEvent.ShowError(R.string.no_internet_connection))
    }

    @Test
    fun `given onTaskDone, when there is a different error, then show mark as done error message`() = runTest {
        initViewModel()
        coEvery { markTaskAsDone.execute(any()) } throws Exception("an exception for you")
        viewModel.onTaskDone(task1)
        coVerify { markTaskAsDone.execute(task1) }
        assertTrue(viewModel.event.first() == TasksEvent.ShowError(R.string.tasks_mark_as_done_error))
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when previous state is loading, then do nothing`() = runTest {
        initViewModel()
        coVerify(exactly = 0) { delegate.hasPendingTasks(any()) }
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when previous state is data and had no pending tasks, then do nothing`() = runTest {
        initViewModel()
        coEvery { delegate.hasPendingTasks(any()) } returnsMany (listOf(false, false))
        viewModel.checkIfHasNoPendingTasks(tasks)
        coVerify(exactly = 2) { delegate.hasPendingTasks(tasks) }
    }

    @Test
    fun `given checkIfHasNoPendingTasks, when previous state is data and had and has still pending tasks, then do nothing`() =
        runTest {
            initViewModel()
            coEvery { delegate.hasPendingTasks(any()) } returnsMany (listOf(true, true))
            viewModel.checkIfHasNoPendingTasks(tasks)
            coVerify(exactly = 2) { delegate.hasPendingTasks(tasks) }
        }

    @Test
    fun `given checkIfHasNoPendingTasks, when had pending tasks and has no pending tasks, then send AllTasksDone event`() =
        runTest {
            initViewModel()
            coEvery { delegate.hasPendingTasks(any()) } returnsMany (listOf(true, false))
            viewModel.checkIfHasNoPendingTasks(tasks)
            coVerify(exactly = 2) { delegate.hasPendingTasks(tasks) }
            assertTrue(viewModel.event.first() == TasksEvent.AllTasksDone)
        }

    companion object {
        private val task1 = Tasc("1", "task 1", false, 0L, 7, false)
        private val task2 = Tasc("2", "task 2", true, 0L, 7, false)
        private val tasks = listOf(task1, task2)
    }

}