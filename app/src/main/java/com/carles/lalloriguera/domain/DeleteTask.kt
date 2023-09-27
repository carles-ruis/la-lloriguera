package com.carles.lalloriguera.domain

import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteTask @Inject constructor(
    private val repository: TaskRepository,
    private val dispatchers: AppDispatchers
) {

    suspend fun execute(taskId: String) = withContext(dispatchers.io) {
        repository.deleteTask(taskId)
    }
}