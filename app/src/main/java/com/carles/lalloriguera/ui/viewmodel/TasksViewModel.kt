package com.carles.lalloriguera.ui.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carles.lalloriguera.R
import com.carles.lalloriguera.data.remote.TimeoutConnectionException
import com.carles.lalloriguera.domain.GetTasks
import com.carles.lalloriguera.domain.MarkTaskAsDone
import com.carles.lalloriguera.model.Tasc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TasksState {
    object Loading : TasksState()
    data class Error(@StringRes val message: Int) : TasksState()
    data class Data(val tasks: List<Tasc>) : TasksState()
}

sealed class TasksEvent {
    object AllTasksDone : TasksEvent()
    data class TaskDone(val taskName: String) : TasksEvent()
    data class ShowError(@StringRes val message: Int): TasksEvent()
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasks: GetTasks,
    private val markTaskAsDone: MarkTaskAsDone,
    private val hasPendingTasksDelegate: HasPendingTasksDelegate
) : ViewModel(), HasPendingTasks by hasPendingTasksDelegate {

    private var _state = MutableStateFlow<TasksState>(TasksState.Loading)
    val state: StateFlow<TasksState> = _state

    private var _event = Channel<TasksEvent>(Channel.BUFFERED)
    val event: Flow<TasksEvent> = _event.receiveAsFlow()

    init {
        getTasks()
    }

    private fun getTasks() {
        _state.value = TasksState.Loading
        getTasks.execute()
            .catch { error ->
                Log.w("TasksViewModel", error.localizedMessage ?: "getTasks error")
                _state.value = TasksState.Error(
                    if (error is TimeoutConnectionException) R.string.no_internet_connection else R.string.tasks_error
                )
            }.onEach { tasks ->
                checkIfHasNoPendingTasks(tasks)
                _state.value = TasksState.Data(tasks)
            }
            .launchIn(viewModelScope)
    }

    fun retry() {
        getTasks()
    }

    fun onTaskDone(task: Tasc) {
        viewModelScope.launch {
            try {
                markTaskAsDone.execute(task)
                _event.send(TasksEvent.TaskDone(task.name))
            } catch (e: Exception) {
                val message = if (e is TimeoutConnectionException) R.string.no_internet_connection else R.string.tasks_mark_as_done_error
                _event.send(TasksEvent.ShowError(message))
            }
        }
    }

    @VisibleForTesting
    suspend fun checkIfHasNoPendingTasks(tasks: List<Tasc>) {
        // if state is a TasksState.Data it is not first load , so user has updated or done a task
        state.value.run {
            if (this is TasksState.Data) {
                val hadPendingTasks = hasPendingTasks(this.tasks)
                val hasPendingTasks = hasPendingTasks(tasks)
                if (hadPendingTasks and !hasPendingTasks) {
                    _event.send(TasksEvent.AllTasksDone)
                }
            }
        }
    }
}