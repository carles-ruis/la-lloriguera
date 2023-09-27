package com.carles.lalloriguera.data

import com.carles.lalloriguera.model.Tasc
import kotlinx.coroutines.flow.Flow

interface TaskDatasource {

    suspend fun getTask(id: String): Tasc

    fun getTasks(): Flow<List<Tasc>>

    suspend fun saveTask(task: Tasc)

    suspend fun updateTask(task: Tasc)

    suspend fun deleteTask(id: String)
}