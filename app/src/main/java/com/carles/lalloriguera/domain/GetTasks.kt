package com.carles.lalloriguera.domain

import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskRepository
import com.carles.lalloriguera.model.Tasc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTasks @Inject constructor(
    private val repository: TaskRepository,
    private val dispatchers: AppDispatchers
) {

    fun execute(): Flow<List<Tasc>> {
        return repository.getTasks().map { tasks ->
            tasks.sortedBy { it.daysRemaining }
        }.flowOn(dispatchers.io)
    }
}