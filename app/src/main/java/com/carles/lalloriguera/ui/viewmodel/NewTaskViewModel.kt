package com.carles.lalloriguera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carles.lalloriguera.domain.NewTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val newTask: NewTask) : ViewModel(), TaskFormHandler by TaskFormDelegate() {

    fun onSaveClick() {
        viewModelScope.launch {
            onSaveClick(action = { task ->
                newTask.execute(task)
            })
        }
    }
}