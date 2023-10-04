package com.carles.lalloriguera.ui.composables

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carles.lalloriguera.R
import com.carles.lalloriguera.common.TimeHelper.Companion.DAYS_TO_MILLIS
import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.ui.extensions.Tags.TASK_CALENDAR_PICKER_DIALOG
import com.carles.lalloriguera.ui.extensions.Tags.TASK_CALENDAR_TEXT_FIELD
import com.carles.lalloriguera.ui.extensions.Tags.TASK_NAME_TEXT_FIELD
import com.carles.lalloriguera.ui.extensions.Tags.TASK_ONE_TIME_CONTENT
import com.carles.lalloriguera.ui.extensions.Tags.TASK_ONE_TIME_TASK_BUTTON
import com.carles.lalloriguera.ui.extensions.Tags.TASK_PERIODICITY_MENU_BOX
import com.carles.lalloriguera.ui.extensions.Tags.TASK_PERIODICITY_TEXT_FIELD
import com.carles.lalloriguera.ui.extensions.Tags.TASK_PERIODIC_CONTENT
import com.carles.lalloriguera.ui.theme.LlorigueraTheme
import com.carles.lalloriguera.ui.viewmodel.TaskFormState
import java.text.SimpleDateFormat
import java.util.*

private val textFieldEnabledColors = @Composable {
    OutlinedTextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun TaskFormContent(
    state: TaskFormState,
    buttonsRow: @Composable () -> Unit,
    onNameChange: (String) -> Unit = {},
    onOneTimeChange: (Boolean) -> Unit = {},
    onLastDateChange: (Long) -> Unit = {},
    onNextDateChange: (Long) -> Unit = {},
    onPeriodicityChange: (Int) -> Unit = {},
) {
    when (state) {
        TaskFormState.ShowProgress -> CenteredProgressIndicator()
        is TaskFormState.Filling -> TaskFormContent(
            task = state.task,
            isValid = state.isValid,
            buttonsRow = buttonsRow,
            onNameChange = onNameChange,
            onLastDateChange = onLastDateChange,
            onNextDateChange = onNextDateChange,
            onOneTimeChange = onOneTimeChange,
            onPeriodicityChange = onPeriodicityChange,
        )
    }
}

@Composable
private fun TaskFormContent(
    task: Tasc,
    isValid: Boolean,
    buttonsRow: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit = {},
    onOneTimeChange: (Boolean) -> Unit = {},
    onLastDateChange: (Long) -> Unit = {},
    onNextDateChange: (Long) -> Unit = {},
    onPeriodicityChange: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        NameTextField(task.name, isValid, onNameChange)
        TaskTypeRadioButtons(task.isOneTime, task.periodicity, onOneTimeChange)
        Box {
            OneTimeTaskContent(task.isOneTime, task.nextDate, onNextDateChange)
            PeriodicTaskContent(task.isOneTime.not(), task.lastDate, task.periodicity, onLastDateChange, onPeriodicityChange)
        }
        buttonsRow()
    }
}

@Composable
private fun NameTextField(name: String, isValid: Boolean, onNameChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = modifier
            .testTag(TASK_NAME_TEXT_FIELD)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        textStyle = MaterialTheme.typography.bodyMedium,
        supportingText = { SupportingText(R.string.new_task_name_description) },
        label = { Text(stringResource(id = R.string.new_task_name_label)) },
        isError = isValid.not(),
        singleLine = true
    )

    if (isValid.not()) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}

@Composable
private fun TaskTypeRadioButtons(
    isOneTime: Boolean,
    periodicity: Int,
    onOneTimeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(4.dp))
                .padding(start = 14.dp, end = 2.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.new_task_periodic),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    SupportingText(pluralStringResource(R.plurals.new_task_periodic_description, periodicity, periodicity))
                }
                RadioButton(selected = isOneTime.not(), onClick = { onOneTimeChange(false) })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.new_task_one_time),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    SupportingText(R.string.new_task_one_time_description)
                }
                RadioButton(
                    selected = isOneTime,
                    onClick = { onOneTimeChange(true) },
                    modifier = Modifier.testTag(TASK_ONE_TIME_TASK_BUTTON)
                )
            }
        }
        Text(
            stringResource(R.string.new_task_type_label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 14.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun OneTimeTaskContent(visible: Boolean, nextDate: Long, onNextDateChange: (Long) -> Unit) {
    var showDialogPicker by remember { mutableStateOf(false) }
    val onDismissDialogPicker = { showDialogPicker = false }
    val onDisplayDialogPicker = { showDialogPicker = true }

    AnimatedVisibility(visible = visible) {
        CalendarTextField(
            dateInMillis = nextDate,
            label = R.string.new_task_next_date,
            supportingText = R.string.new_task_next_date_description,
            onDisplayDialog = onDisplayDialogPicker,
            modifier = Modifier
                .testTag(TASK_ONE_TIME_CONTENT)
                .semantics(mergeDescendants = false) {}
        )
    }
    if (showDialogPicker) {
        DatePickerDialog(
            initialDate = nextDate,
            selectableDaysInThePast = 1,
            selectableDaysInTheFuture = 30,
            onDateSelected = onNextDateChange,
            onDismissDialog = onDismissDialogPicker
        )
    }
}


@Composable
private fun PeriodicTaskContent(
    visible: Boolean,
    lastDate: Long,
    periodicity: Int,
    onLastDateChange: (Long) -> Unit,
    onPeriodicityChange: (Int) -> Unit
) {
    var showDialogPicker by remember { mutableStateOf(false) }
    val onDismissDialogPicker = { showDialogPicker = false }
    val onDisplayDialogPicker = { showDialogPicker = true }

    AnimatedVisibility(visible = visible) {
        Column(
            verticalArrangement = Arrangement.spacedBy(26.dp),
            modifier = Modifier.testTag(TASK_PERIODIC_CONTENT)
        ) {
            PeriodicityTextField(periodicity, onPeriodicityChange)
            CalendarTextField(
                dateInMillis = lastDate,
                label = R.string.new_task_last_date,
                supportingText = R.string.new_task_last_date_description,
                onDisplayDialog = onDisplayDialogPicker
            )
        }
    }
    if (showDialogPicker) {
        DatePickerDialog(
            initialDate = lastDate,
            selectableDaysInThePast = 31,
            selectableDaysInTheFuture = 0,
            onDateSelected = onLastDateChange,
            onDismissDialog = onDismissDialogPicker
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodicityTextField(
    periodicity: Int,
    onPeriodicityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { newValue -> expanded = newValue },
        modifier = Modifier.testTag(TASK_PERIODICITY_MENU_BOX)
    ) {
        OutlinedTextField(
            value = periodicity.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier
                .testTag(TASK_PERIODICITY_TEXT_FIELD)
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            supportingText = {
                SupportingText(R.string.new_task_periodicity_description)
            },
            label = { Text(stringResource(id = R.string.new_task_periodicity)) },
        )
        DropdownMenu(
            modifier = Modifier.exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            (1..30).forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toString(), style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onPeriodicityChange(item)
                        expanded = false
                    })
            }
        }
    }
}


@Composable
private fun CalendarTextField(
    dateInMillis: Long,
    @StringRes label: Int,
    @StringRes supportingText: Int,
    onDisplayDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    OutlinedTextField(
        modifier = modifier
            .testTag(TASK_CALENDAR_TEXT_FIELD)
            .semantics(mergeDescendants = false) {}
            .fillMaxWidth()
            .clickable { onDisplayDialog() },
        enabled = false,
        colors = textFieldEnabledColors(),
        readOnly = true,
        value = formatter.format(Date(dateInMillis)),
        onValueChange = {},
        label = { Text(stringResource(label)) },
        supportingText = { SupportingText(supportingText) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    initialDate: Long,
    selectableDaysInThePast: Int,
    selectableDaysInTheFuture: Int,
    onDateSelected: (Long) -> Unit = {},
    onDismissDialog: () -> Unit = {}
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val now = System.currentTimeMillis()
                val firstSelectableDate = now - selectableDaysInThePast * DAYS_TO_MILLIS
                val lastSelectableDate = now + selectableDaysInTheFuture * DAYS_TO_MILLIS
                return utcTimeMillis in firstSelectableDate..lastSelectableDate
            }
        }
    )

    val confirmButton = @Composable {
        TextButton(onClick = {
            onDismissDialog()
            onDateSelected(state.selectedDateMillis ?: initialDate)
        }) {
            Text(stringResource(R.string.ok))
        }
    }

    val dismissButton = @Composable {
        TextButton(onClick = { onDismissDialog() }) {
            Text(stringResource(R.string.cancel), style = MaterialTheme.typography.labelLarge)
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        modifier = Modifier.testTag(TASK_CALENDAR_PICKER_DIALOG)
    ) { DatePicker(state = state) }
}

// region previews
@Preview
@Composable
private fun TaskFormContent_oneTimeTask() {
    LlorigueraTheme {
        TaskFormContent(
            task = Tasc("3", "Penjar el mirall", true, System.currentTimeMillis(), 10, true),
            isValid = true,
            buttonsRow = {}
        )
    }
}

@Preview
@Composable
private fun TaskFormContent_periodicTask() {
    LlorigueraTheme {
        TaskFormContent(
            task = Tasc("0", "Posar la rentadora", false, System.currentTimeMillis(), 7, false),
            isValid = true,
            buttonsRow = {}
        )
    }
}

@Preview
@Composable
private fun DatePickerDialog_oneTimeTask() {
    LlorigueraTheme {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp), Alignment.Center
        ) {
            DatePickerDialog(
                initialDate = System.currentTimeMillis(),
                selectableDaysInThePast = 0,
                selectableDaysInTheFuture = 30
            )
        }
    }
}

@Preview
@Composable
private fun DatePickerDialog_periodicTask() {
    LlorigueraTheme {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp), Alignment.Center
        ) {
            DatePickerDialog(
                initialDate = System.currentTimeMillis(),
                selectableDaysInThePast = 30,
                selectableDaysInTheFuture = 0,
            )
        }
    }
}

//endregion previews








