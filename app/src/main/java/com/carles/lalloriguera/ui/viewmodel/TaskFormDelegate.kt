package com.carles.lalloriguera.ui.viewmodel

import androidx.annotation.StringRes
import com.carles.lalloriguera.R
import com.carles.lalloriguera.common.TimeHelper.Companion.getDaysBetweenDates
import com.carles.lalloriguera.data.remote.NoConnectionCancellationException
import com.carles.lalloriguera.model.Tasc
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

private const val DEFAULT_PERIODICITY = 7

sealed class TaskFormState {
    object ShowProgress : TaskFormState()
    data class Filling(val task: Tasc, val isValid: Boolean) : TaskFormState()
}

sealed class TaskFormEvent {
    data class ShowError(@StringRes val message: Int, val exit: Boolean = false) : TaskFormEvent()
    data class Saved(val taskName: String) : TaskFormEvent()
    data class Deleted(val taskName: String) : TaskFormEvent()
}

interface TaskFormHandler {

    val event: Flow<TaskFormEvent>
    val state: StateFlow<TaskFormState>

    fun setLoadingState()
    suspend fun sendShowErrorEvent(@StringRes message: Int, exit: Boolean = false)
    suspend fun sendDeletedEvent()
    fun initTask(task: Tasc)
    fun onNameChange(name: String)
    fun onOneTimeChange(isOneTime: Boolean)
    fun onLastDateChange(lastDate: Long)
    fun onNextDateChange(nextDate: Long)
    fun onPeriodicityChange(periodicity: Int)
    fun onNotificationsChange(notificationsOn: Boolean)
    suspend fun onSaveClick(action: suspend (Tasc) -> Unit)
}

class TaskFormDelegate @Inject constructor() : TaskFormHandler {

    private var task = Tasc(
        id = null,
        name = "",
        isOneTime = false,
        lastDate = System.currentTimeMillis(),
        periodicity = DEFAULT_PERIODICITY,
        notificationsOn = false
    )
        set(value) {
            field = value
            updateForm()
        }

    private var isValid = true
        set(value) {
            field = value
            updateForm()
        }

    private var _event = Channel<TaskFormEvent>(Channel.BUFFERED)
    override val event: Flow<TaskFormEvent> = _event.receiveAsFlow()

    private var _state = MutableStateFlow<TaskFormState>(TaskFormState.Filling(task, isValid))
    override val state: StateFlow<TaskFormState> = _state

    private fun updateForm() {
        _state.value = TaskFormState.Filling(task, isValid)
    }

    override fun setLoadingState() {
        _state.value = TaskFormState.ShowProgress
    }

    override suspend fun sendShowErrorEvent(@StringRes message: Int, exit: Boolean) {
        _event.send(TaskFormEvent.ShowError(message, exit))
    }

    override suspend fun sendDeletedEvent() {
        _event.send(TaskFormEvent.Deleted(task.name))
    }

    override fun initTask(task: Tasc) {
        this.task = task
    }

    override fun onNameChange(name: String) {
        task = task.copy(name = name)
        isValid = true
    }

    override fun onOneTimeChange(isOneTime: Boolean) {
        task = task.copy(isOneTime = isOneTime, lastDate = System.currentTimeMillis(), periodicity = DEFAULT_PERIODICITY)
    }

    override fun onLastDateChange(lastDate: Long) {
        require(task.isOneTime.not()) { "Cannot change last date on a one time task" }
        task = task.copy(lastDate = lastDate)
    }

    override fun onNextDateChange(nextDate: Long) {
        require(task.isOneTime) { "Cannot change next date on a periodic task" }
        val now = System.currentTimeMillis()
        val daysRemaining = getDaysBetweenDates(now, nextDate)
        task = task.copy(lastDate = now, periodicity = daysRemaining)
    }

    override fun onPeriodicityChange(periodicity: Int) {
        task = task.copy(periodicity = periodicity)
    }

    override fun onNotificationsChange(notificationsOn: Boolean) {
        task = task.copy(notificationsOn = notificationsOn)
    }

    override suspend fun onSaveClick(action: suspend (Tasc) -> Unit) {
        if (task.name.isEmpty()) {
            isValid = false
            updateForm()
        } else {
            _state.value = TaskFormState.ShowProgress
            try {
                action(task)
                _event.send(TaskFormEvent.Saved(task.name))
            } catch (e: Exception) {
                _state.value = TaskFormState.Filling(task, true)
                val message =
                    if (e is NoConnectionCancellationException) R.string.no_internet_connection else R.string.edit_task_save_error
                _event.send(TaskFormEvent.ShowError(message))
            }
        }
    }
}