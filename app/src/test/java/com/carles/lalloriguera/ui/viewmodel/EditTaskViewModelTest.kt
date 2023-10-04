package com.carles.lalloriguera.ui.viewmodel

import MainDispatcherRule
import androidx.lifecycle.SavedStateHandle
import com.carles.lalloriguera.R
import com.carles.lalloriguera.data.remote.NoConnectionCancellationException
import com.carles.lalloriguera.domain.DeleteTask
import com.carles.lalloriguera.domain.GetTask
import com.carles.lalloriguera.domain.UpdateTask
import com.carles.lalloriguera.model.Tasc
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class EditTaskViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val getTask: GetTask = mockk()
    private val updateTask: UpdateTask = mockk()
    private val deleteTask: DeleteTask = mockk()
    private val delegate: TaskFormDelegate = mockk()
    private val savedState = SavedStateHandle().apply { set("taskId", taskId) }
    private lateinit var viewModel: EditTaskViewModel

    @Test
    fun `given view model created, when get task is successful, then init task`() = runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } returns task
        coEvery { delegate.initTask(any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)
        coVerify { delegate.setLoadingState() }
        coVerify { getTask.execute(taskId) }
        coVerify { delegate.initTask(task) }
    }

    @Test
    fun `given view model created, when get task returns a no connection error, then show no connection error message`() =
        runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } throws NoConnectionCancellationException()
        coEvery { delegate.sendShowErrorEvent(any(), any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)
        coVerify { delegate.sendShowErrorEvent(R.string.no_internet_connection, true) }
    }

    @Test
    fun `given view model created, when get task returns a different error, then show load error message`() = runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } throws Exception("what an error!")
        coEvery { delegate.sendShowErrorEvent(any(), any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)
        coVerify { delegate.sendShowErrorEvent(R.string.edit_task_load_error, true) }
    }

    @Test
    fun `given onSaveClick, when called, then update the task`() = runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } returns task
        coEvery { delegate.initTask(any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)

        coEvery { delegate.onSaveClick(any()) } coAnswers {
            firstArg<suspend (Tasc) -> Unit>().invoke(task)
        }
        coEvery { updateTask.execute(any()) } just Runs
        viewModel.onSaveClick()
        coVerify { updateTask.execute(task) }
    }

    @Test
    fun `given onDeleteClick, when delete is successful, then delete the task`() = runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } returns task
        coEvery { delegate.initTask(any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)

        coEvery { deleteTask.execute(any()) } just Runs
        coEvery { delegate.sendDeletedEvent() } just Runs
        viewModel.onDeleteClick()
        coVerify { deleteTask.execute(taskId) }
        coVerify { delegate.sendDeletedEvent() }
    }

    @Test
    fun `given onDeleteClick, when delete throws a no connection error, then show no connection error message`() = runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } returns task
        coEvery { delegate.initTask(any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)

        coEvery { deleteTask.execute(any()) } throws NoConnectionCancellationException()
        coEvery { delegate.sendShowErrorEvent(any(), any()) } just Runs
        viewModel.onDeleteClick()
        coVerify { deleteTask.execute(taskId) }
        coVerify { delegate.sendShowErrorEvent(R.string.no_internet_connection) }
    }

    @Test
    fun `given onDeleteClick, when delete throws a different error, then show delete error message`() = runTest {
        coEvery { delegate.setLoadingState() } just Runs
        coEvery { getTask.execute(any()) } returns task
        coEvery { delegate.initTask(any()) } just Runs
        viewModel = EditTaskViewModel(getTask, updateTask, deleteTask, delegate, savedState)

        coEvery { deleteTask.execute(any()) } throws Exception("the best error")
        coEvery { delegate.sendShowErrorEvent(any(), any()) } just Runs
        viewModel.onDeleteClick()
        coVerify { deleteTask.execute(taskId) }
        coVerify { delegate.sendShowErrorEvent(R.string.edit_task_delete_error) }
    }

    companion object {
        private const val taskId = "1"
        private val task = Tasc(taskId, "the task", false, 0L, 7, false)
    }
}