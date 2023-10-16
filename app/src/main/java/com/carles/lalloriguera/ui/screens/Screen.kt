package com.carles.lalloriguera.ui.screens

import androidx.annotation.StringRes
import com.carles.lalloriguera.R
import com.carles.lalloriguera.ui.screens.Arguments.taskId

sealed class Screen(
    val route: String,
    @StringRes val title: Int,
    val showUpButton: Boolean
) {
    object SignIn: Screen("signin_route", R.string.signin_title, false)
    object Tasks : Screen("tasks_list_route", R.string.tasks_title, false)
    object Conill : Screen("conill_route", R.string.conill_title, true)
    object NewTask : Screen("add_task_route", R.string.new_task_title, true)
    object EditTask : Screen("edit_task_route/{$taskId}", R.string.edit_task_title, true) {
        fun navigationRoute(id: String) = "edit_task_route/$id"
    }
}

object Arguments {
    const val taskId = "argumentTaskId"
}