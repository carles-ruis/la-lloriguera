package com.carles.lalloriguera.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carles.lalloriguera.R
import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.ui.composables.TaskFormContent
import com.carles.lalloriguera.ui.extensions.Tags.TASK_SAVE_BUTTON
import com.carles.lalloriguera.ui.extensions.showToast
import com.carles.lalloriguera.ui.theme.LlorigueraTheme
import com.carles.lalloriguera.ui.viewmodel.NewTaskViewModel
import com.carles.lalloriguera.ui.viewmodel.TaskFormEvent
import com.carles.lalloriguera.ui.viewmodel.TaskFormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NewTaskScreen(
    viewModel: NewTaskViewModel,
    navigateUp: () -> Unit,
) {
    val eventFlow = viewModel.event
    NewTaskScreenEventHandler(eventFlow, navigateUp)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNameChange: (String) -> Unit = { name -> viewModel.onNameChange(name) }
    val onOneTimeChange: (Boolean) -> Unit = { isOneTime -> viewModel.onOneTimeChange(isOneTime) }
    val onLastDateChange: (Long) -> Unit = { lastDate -> viewModel.onLastDateChange(lastDate) }
    val onNextDateChange: (Long) -> Unit = { nextDate -> viewModel.onNextDateChange(nextDate) }
    val onPeriodicityChange: (Int) -> Unit = { periodicity -> viewModel.onPeriodicityChange(periodicity) }
    val onSaveClick = { viewModel.onSaveClick() }

    TaskFormContent(
        state = state,
        buttonsRow = { SaveButtonBox(onSaveClick = onSaveClick) },
        onNameChange = onNameChange,
        onOneTimeChange = onOneTimeChange,
        onLastDateChange = onLastDateChange,
        onNextDateChange = onNextDateChange,
        onPeriodicityChange = onPeriodicityChange,
    )
}

@Composable
private fun NewTaskScreenEventHandler(eventFlow: Flow<TaskFormEvent>, navigateUp: () -> Unit) {
    val context = LocalContext.current
    val resources = context.resources

    LaunchedEffect(Unit) {
        eventFlow.collectLatest { event ->
            when (event) {
                is TaskFormEvent.ShowError -> {
                    context.showToast(resources.getString(event.message))
                    if (event.exit) navigateUp()
                }

                is TaskFormEvent.Saved -> {
                    context.showToast(resources.getString(R.string.new_task_saved, event.taskName.uppercase()))
                    navigateUp()
                }

                else -> {}
            }
        }
    }
}


@Composable
private fun SaveButtonBox(modifier: Modifier = Modifier, onSaveClick: () -> Unit = {}) {
    Box(modifier.fillMaxWidth(), Alignment.Center) {
        ElevatedButton(
            onClick = onSaveClick,
            modifier = modifier.testTag(TASK_SAVE_BUTTON)
        ) {
            Text(stringResource(R.string.new_task_save))
        }
    }
}

@Preview
@Composable
private fun TaskFormContent_WithSaveButtonBox() {
    LlorigueraTheme {
        TaskFormContent(
            state = TaskFormState.Filling(
                task = Tasc(null, "", false, System.currentTimeMillis(), 7, false),
                isValid = false
            ),
            buttonsRow = { SaveButtonBox() }
        )
    }
}

