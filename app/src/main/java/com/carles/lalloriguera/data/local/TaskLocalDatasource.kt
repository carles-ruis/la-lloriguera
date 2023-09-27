package com.carles.lalloriguera.data.local

import com.carles.lalloriguera.data.TaskDatasource
import com.carles.lalloriguera.model.Tasc
import com.carles.lalloriguera.data.TaskMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskLocalDatasource @Inject constructor(
    private val taskMapper: TaskMapper,
    private val taskDao: TaskDao
) : TaskDatasource {

    override suspend fun getTask(id: String): Tasc {
        return taskMapper.fromEntity(taskDao.loadTask(id.toInt()))
    }

    override fun getTasks(): Flow<List<Tasc>> {
        return taskDao.loadTasks().map { task ->
            task.map {
                taskMapper.fromEntity(it)
            }
        }
    }

    override suspend fun saveTask(task: Tasc) {
        taskDao.saveTask(taskMapper.toEntity(task))
    }

    override suspend fun updateTask(task: Tasc) {
        taskDao.updateTask(taskMapper.toEntity(task))
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteTask(id.toInt())
    }
}