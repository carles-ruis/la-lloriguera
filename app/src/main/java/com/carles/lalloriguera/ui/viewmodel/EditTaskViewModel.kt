package com.carles.lalloriguera.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carles.lalloriguera.R
import com.carles.lalloriguera.data.remote.NoConnectionCancellationException
import com.carles.lalloriguera.domain.DeleteTask
import com.carles.lalloriguera.domain.GetTask
import com.carles.lalloriguera.domain.UpdateTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val getTask: GetTask,
    private val updateTask: UpdateTask,
    private val deleteTask: DeleteTask,
    savedStateHandle: SavedStateHandle
) : ViewModel(), TaskFormHandler by TaskFormDelegate() {

    private val taskId = savedStateHandle.get<String>("taskId") ?: "0"

    init {
        getTask()
    }

    private fun getTask() {
        viewModelScope.launch {
            try {
                setLoadingState()
                initTask(getTask.execute(taskId))
            } catch (e: Exception) {
                Log.w("EditTaskViewModel", e.localizedMessage ?: "getTask error")
                val message =
                    if (e is NoConnectionCancellationException) R.string.no_internet_connection else R.string.edit_task_load_error
                sendShowErrorEvent(message, exit = true)
            }
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            onSaveClick(action = { task ->
                updateTask.execute(task)
            })
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            try {
                deleteTask.execute(taskId)
                sendDeletedEvent()
            } catch (e: Exception) {
                val message =
                    if (e is NoConnectionCancellationException) R.string.no_internet_connection else R.string.edit_task_delete_error
                sendShowErrorEvent(message)
            }
        }
    }
}