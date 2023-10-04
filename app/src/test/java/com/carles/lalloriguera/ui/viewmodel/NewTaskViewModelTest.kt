package com.carles.lalloriguera.ui.viewmodel

import MainDispatcherRule
import com.carles.lalloriguera.domain.NewTask
import com.carles.lalloriguera.model.Tasc
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewTaskViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val newTask: NewTask = mockk()
    private val delegate: TaskFormDelegate = mockk()
    private lateinit var viewModel: NewTaskViewModel

    @Before
    fun setup() {
        viewModel = NewTaskViewModel(newTask, delegate)
    }

    @Test
    fun `given onSaveClick, when called, then create new task`() = runTest {
        coEvery { delegate.onSaveClick(any()) } coAnswers {
            firstArg<suspend (Tasc) -> Unit>().invoke(task)
        }
        coEvery { newTask.execute(any()) } just Runs
        viewModel.onSaveClick()
        coVerify { delegate.onSaveClick(any()) }
        coVerify { newTask.execute(task) }
    }

    companion object {
        private val task = Tasc("1", "new task", true, 0L, 7, false)
    }

}