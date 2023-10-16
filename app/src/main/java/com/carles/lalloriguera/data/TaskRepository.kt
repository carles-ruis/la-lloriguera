package com.carles.lalloriguera.data

import com.carles.lalloriguera.model.Tasc
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val datasource: TaskDatasource) {

    suspend fun getTask(id: String): Tasc {
        return datasource.getTask(id)
    }

    fun getTasks(): Flow<List<Tasc>> {
        return datasource.getTasks()
    }

    suspend fun saveTask(task: Tasc) {
        return datasource.saveTask(task)
    }

    suspend fun updateTask(task: Tasc) {
        return datasource.updateTask(task)
    }

    suspend fun deleteTask(id: String) {
        return datasource.deleteTask(id)
    }

}