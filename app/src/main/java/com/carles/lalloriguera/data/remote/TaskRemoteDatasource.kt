package com.carles.lalloriguera.data.remote

import com.carles.lalloriguera.data.TaskDatasource
import com.carles.lalloriguera.data.TaskMapper
import com.carles.lalloriguera.model.Tasc
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRemoteDatasource @Inject constructor(
    private val database: DatabaseReference,
    private val taskMapper: TaskMapper
) : TaskDatasource {

    override suspend fun getTask(id: String): Tasc {
        checkConnection()
        val reference = database.child(TASKS_NODE).child(id)
        val taskRef = reference.singleValueEvent(TaskRef::class.java)
        return taskMapper.fromRef(taskRef)
    }

    override fun getTasks(): Flow<List<Tasc>> {
        val reference = database.child(TASKS_NODE)
        return reference.flowList(TaskRef::class.java).map { list ->
            list.map { taskRef ->
                taskMapper.fromRef(taskRef)
            }
        }
    }

    override suspend fun saveTask(task: Tasc) {
        checkConnection()
        val taskId = database.generateNodeId()
        val taskRef = taskMapper.toRef(task).copy(id = taskId)
        database.child(TASKS_NODE).child(taskId).setValue(taskRef).setValueListeners()
    }

    override suspend fun updateTask(task: Tasc) {
        checkConnection()
        if (task.id == null) {
            throw CancellationException("Error updating task. Task id not set")
        } else {
            val taskRef = taskMapper.toRef(task)
            database.child(TASKS_NODE).child(task.id).setValue(taskRef).setValueListeners()
        }
    }

    override suspend fun deleteTask(id: String) {
        checkConnection()
        database.child(TASKS_NODE).child(id).removeValue().setValueListeners()
    }

    private suspend fun checkConnection() {
        database.waitForConnection()
    }

    companion object {
        private const val TASKS_NODE = "tasks"
    }
}
