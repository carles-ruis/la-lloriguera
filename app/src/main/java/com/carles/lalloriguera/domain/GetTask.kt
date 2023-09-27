package com.carles.lalloriguera.domain

import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import com.carles.lalloriguera.model.Tasc
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTask @Inject constructor(
    private val repository: TaskRepository,
    private val dispatchers: AppDispatchers
) {

    suspend fun execute(taskId: String): Tasc = withContext(dispatchers.io) {
        repository.getTask(taskId)
    }
}