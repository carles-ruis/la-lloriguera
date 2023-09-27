package com.carles.lalloriguera.ui.viewmodel

import com.carles.lalloriguera.model.Tasc
import javax.inject.Inject

interface HasPendingTasks {
    fun hasPendingTasks(tasks: List<Tasc>): Boolean
}

class HasPendingTasksDelegate @Inject constructor() : HasPendingTasks {

    override fun hasPendingTasks(tasks: List<Tasc>): Boolean {
        return tasks.any { it.daysRemaining < 0 }
    }
}