package com.carles.lalloriguera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carles.lalloriguera.R
import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.ui.composables.TaskFormContent
import com.carles.lalloriguera.ui.extensions.Tags
import com.carles.lalloriguera.ui.extensions.Tags.TASK_DELETE_CONFIRMATION_DIALOG
import com.carles.lalloriguera.ui.extensions.showToast
import com.carles.lalloriguera.ui.theme.LlorigueraTheme
import com.carles.lalloriguera.ui.viewmodel.EditTaskViewModel
import com.carles.lalloriguera.ui.viewmodel.TaskFormEvent
import com.carles.lalloriguera.ui.viewmodel.TaskFormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditTaskScreen(
    viewModel: EditTaskViewModel,
    navigateUp: () -> Unit,
) {
    val eventFlow = viewModel.event
    EditTaskScreenEventHandler(eventFlow, navigateUp)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val taskName by remember {
        derivedStateOf {
            (state as? TaskFormState.Filling)?.task?.name ?: ""
        }
    }
    val onNameChange: (String) -> Unit = { name -> viewModel.onNameChange(name) }
    val onOneTimeChange: (Boolean) -> Unit = { isOneTime -> viewModel.onOneTimeChange(isOneTime) }
    val onLastDateChange: (Long) -> Unit = { lastDate -> viewModel.onLastDateChange(lastDate) }
    val onNextDateChange: (Long) -> Unit = { nextDate -> viewModel.onNextDateChange(nextDate) }
    val onPeriodicityChange: (Int) -> Unit = { periodicity -> viewModel.onPeriodicityChange(periodicity) }
    val onSaveClick = { viewModel.onSaveClick() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val onDeleteConfirmed = { viewModel.onDeleteClick() }
    val onDeleteClick = { showDeleteDialog = true }
    val onDeleteDismissed = { showDeleteDialog = false }

    TaskFormContent(
        state = state,
        buttonsRow = { ButtonsRow(Modifier, onSaveClick, onDeleteClick) },
        onNameChange = onNameChange,
        onOneTimeChange = onOneTimeChange,
        onLastDateChange = onLastDateChange,
        onNextDateChange = onNextDateChange,
        onPeriodicityChange = onPeriodicityChange,
    )

    if (showDeleteDialog) {
        DeleteConfirmationDialog(taskName, Modifier, onDeleteConfirmed, onDeleteDismissed)
    }
}

@Composable
private fun EditTaskScreenEventHandler(eventFlow: Flow<TaskFormEvent>, navigateUp: () -> Unit) {
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
                    context.showToast(resources.getString(R.string.edit_task_saved, event.taskName.uppercase()))
                    navigateUp()
                }

                is TaskFormEvent.Deleted -> {
                    context.showToast(resources.getString(R.string.edit_task_deleted, event.taskName.uppercase()))
                    navigateUp()
                }
            }
        }
    }
}

@Composable
private fun ButtonsRow(modifier: Modifier = Modifier, onSaveClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        OutlinedButton(
            onClick = { onDeleteClick() },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.testTag(Tags.TASK_DELETE_BUTTON)
        ) {
            Icon(Icons.Default.Delete, stringResource(id = R.string.edit_task_delete))
        }
        ElevatedButton(
            onClick = onSaveClick,
            modifier = Modifier.testTag(Tags.TASK_UPDATE_BUTTON)
        ) {
            Text(stringResource(R.string.edit_task_save))
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    taskName: String,
    modifier: Modifier = Modifier,
    onDeleteConfirmed: () -> Unit = {},
    onDeleteDismissed: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDeleteDismissed,
        confirmButton = {
            ElevatedButton(onClick = onDeleteConfirmed) {
                Text(stringResource(R.string.yes))
            }
        },
        modifier = modifier.testTag(TASK_DELETE_CONFIRMATION_DIALOG),
        dismissButton = {
            TextButton(onClick = onDeleteDismissed) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = {
            Text(
                stringResource(R.string.edit_task_delete),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        text = {
            Text(stringResource(R.string.edit_task_delete_confirmation, taskName))
        }
    )
}


@Composable
@Preview
private fun TaskFormContent_WithButtonsRow() {
    LlorigueraTheme {
        TaskFormContent(
            state = TaskFormState.Filling(
                task = Tasc("1", "Some Task", false, System.currentTimeMillis(), 7, true),
                isValid = true
            ),
            buttonsRow = { ButtonsRow() }
        )
    }
}

@Composable
@Preview
private fun DeleteConfirmationDialogPreview() {
    LlorigueraTheme {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp), Alignment.Center
        ) {
            DeleteConfirmationDialog("Regar les plantes")
        }
    }
}
