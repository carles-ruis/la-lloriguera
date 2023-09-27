package com.carles.lalloriguera.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carles.lalloriguera.R
import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.ui.composables.CenteredProgressIndicator
import com.carles.lalloriguera.ui.composables.RetrySnackBar
import com.carles.lalloriguera.ui.extensions.OrientationPreviews
import com.carles.lalloriguera.ui.extensions.Tags.EMPTY_TASKS_BOX
import com.carles.lalloriguera.ui.extensions.Tags.NEW_TASK_BUTTON
import com.carles.lalloriguera.ui.extensions.Tags.TASKS_LIST
import com.carles.lalloriguera.ui.extensions.Tags.TASKS_LIST_ROW
import com.carles.lalloriguera.ui.extensions.Tags.TASK_DONE_ICON
import com.carles.lalloriguera.ui.extensions.Tags.TASK_EDIT_ICON
import com.carles.lalloriguera.ui.extensions.conditional
import com.carles.lalloriguera.ui.extensions.showToast
import com.carles.lalloriguera.ui.theme.LlorigueraTheme
import com.carles.lalloriguera.ui.viewmodel.TasksEvent
import com.carles.lalloriguera.ui.viewmodel.TasksState
import com.carles.lalloriguera.ui.viewmodel.TasksViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.absoluteValue

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onNewTaskClick: () -> Unit,
    onEditTaskClick: (String) -> Unit,
    onNoPendingTasks: () -> Unit
) {
    val eventFlow = viewModel.event
    TasksScreenEventHandler(eventFlow, onNoPendingTasks)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationsPermission()
    }

    val state: TasksState by viewModel.state.collectAsStateWithLifecycle()
    val onRetry = { viewModel.retry() }
    val onTaskDoneClick: (Tasc) -> Unit = { task -> viewModel.onTaskDone(task) }

    state.run {
        when (this) {
            TasksState.Loading -> CenteredProgressIndicator()
            is TasksState.Error -> RetrySnackBar(message, onRetry)
            is TasksState.Data -> TasksContent(
                tasks = tasks,
                onNewTaskClick = onNewTaskClick,
                onEditTaskClick = onEditTaskClick,
                onTaskDoneClick = onTaskDoneClick
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun RequestNotificationsPermission() {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        Log.i("RequestNotificationsPermission", "permission granted by the user?$isGranted")
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                /* context = */ context,
                /* permission = */ Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("RequestNotificationsPermission", "requesting permission to the user")
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun TasksScreenEventHandler(eventFlow: Flow<TasksEvent>, onNoPendingTasks: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        eventFlow.collectLatest { event ->
            when (event) {
                TasksEvent.AllTasksDone -> onNoPendingTasks()
                is TasksEvent.TaskDone -> context.showToast(
                    context.resources.getString(
                        R.string.tasks_done,
                        event.taskName.uppercase()
                    )
                )

                is TasksEvent.ShowError -> context.showToast(context.resources.getString(event.message))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun TasksContent(
    tasks: List<Tasc>,
    modifier: Modifier = Modifier,
    onNewTaskClick: () -> Unit = {},
    onEditTaskClick: (String) -> Unit = {},
    onTaskDoneClick: (Tasc) -> Unit = {}
) {
    Scaffold(
        floatingActionButton = { NewTaskButton(onNewTaskClick) },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        if (tasks.isEmpty()) {
            EmptyTasksBox()
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize().testTag(TASKS_LIST),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = tasks,
                    key = { index: Int, task: Tasc -> if (index == 0) 0 else task.id!! }) { _: Int, task: Tasc ->
                    TaskRow(
                        task = task,
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(horizontal = 8.dp),
                        onEditTaskClick = onEditTaskClick,
                        onTaskDoneClick = onTaskDoneClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTasksBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .testTag(EMPTY_TASKS_BOX)
            .padding(16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.tasks_no_tasks),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TaskRow(
    task: Tasc,
    modifier: Modifier = Modifier,
    onEditTaskClick: (String) -> Unit,
    onTaskDoneClick: (Tasc) -> Unit
) {
    Box(
        modifier = modifier.testTag(TASKS_LIST_ROW),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                    shape = CircleShape
                )
                .conditional(
                    condition = task.daysRemaining < 0,
                    ifTrue = {
                        Modifier.background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = CircleShape
                        )
                    })
                .padding(start = 8.dp)
                .height(72.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = task.daysRemaining.absoluteValue.toString(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 4.dp, end = 12.dp)
            )
            Box(
                modifier = Modifier
                    .testTag(TASK_EDIT_ICON)
                    .size(48.dp)
                     .clickable { onEditTaskClick(task.id!!) },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Edit, stringResource(id = R.string.tasks_edit)) }
            Box(
                modifier = Modifier
                    .testTag(TASK_DONE_ICON)
                    .size(48.dp)
                    .clickable { onTaskDoneClick(task) },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Done, stringResource(id = R.string.tasks_mark_as_done)) }
        }
        if (task.isOneTime) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.tasks_one_time),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun NewTaskButton(onNewTaskClick: () -> Unit, modifier: Modifier = Modifier) {
    var isClicked by remember { mutableStateOf(false) }

    FloatingActionButton(
        modifier = modifier.testTag(NEW_TASK_BUTTON),
        onClick = {
            if (isClicked.not()) {
                isClicked = true
                onNewTaskClick()
            }
        },
    ) {
        Icon(Icons.Default.Add, stringResource(R.string.new_task_title))
    }
}

private fun previewTaskList(): List<Tasc> {
    fun Long.minusDays(days: Int) = this - days.toLong() * 24 * 60 * 60 * 1_000
    val now = System.currentTimeMillis()
    return listOf(
        Tasc("0", "Posar la rentadora", false, now, 7, false),
        Tasc("1", "Regar les plantes", false, now.minusDays(3), 7, false),
        Tasc("2", "Netejar el microones", false, now.minusDays(15), 15, false),
        Tasc("3", "Penjar el mirall", true, now.minusDays(20), 10, true),
        Tasc("4", "Exfoliacio conjunta", false, now.minusDays(5), 15, false),
        Tasc("5", "Veure una pel.licula", false, now.minusDays(8), 7, false),
        Tasc("6", "Donar menjar als coloms", false, now.minusDays(30), 15, false),
    )
}

@OrientationPreviews
@Composable
private fun TasksContent_SomeTasks() {
    LlorigueraTheme {
        TasksContent(previewTaskList())
    }
}

@Preview
@Composable
private fun TasksContent_NoTasks() {
    LlorigueraTheme {
        TasksContent(emptyList())
    }
}