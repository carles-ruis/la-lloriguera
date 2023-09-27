package com.carles.lalloriguera.data.remote

import com.carles.lalloriguera.data.TaskMapper
import com.carles.lalloriguera.model.Tasc
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TaskRemoteDatasourceTest {

    private val database: DatabaseReference = mockk()
    private val mapper: TaskMapper = mockk()
    private val tasksNode: DatabaseReference = mockk()
    private val taskNode: DatabaseReference = mockk()
    private val databaseExtensions = "com.carles.lalloriguera.data.remote.DatabaseExtensionsKt"
    private val task: Task<Void> = mockk()
    private lateinit var datasource: TaskRemoteDatasource

    @Before
    fun setup() {
        datasource = TaskRemoteDatasource(database, mapper)
        mockkStatic(databaseExtensions)
    }

    @Test
    fun `given getTask, when id is provided, then obtain task from the remote database`() = runTest {
        coEvery { database.waitForConnection(any()) } returns true
        coEvery { database.child(any()) } returns tasksNode
        coEvery { tasksNode.child(any()) } returns taskNode
        coEvery { taskNode.singleValueEvent(any<Class<TaskRef>>()) } returns taskRef1
        coEvery { mapper.fromRef(any()) } returns task1

        val result = datasource.getTask(taskId)

        coVerify { database.waitForConnection() }
        coVerify { database.child(TASKS_NODE) }
        coVerify { tasksNode.child(taskId) }
        coVerify { taskNode.singleValueEvent(TaskRef::class.java) }
        coVerify { mapper.fromRef(taskRef1) }
        assertEquals(task1, result)
    }

    @Test
    fun `given getTasks, when called, then return list of tasks as a flow`() = runTest {
        val tasksRef = listOf(taskRef1, taskRef2)
        val tasks = listOf(task1, task2)

        coEvery { database.child(TASKS_NODE) } returns tasksNode
        coEvery { tasksNode.flowList(TaskRef::class.java) } returns flow { emit(tasksRef) }
        coEvery { mapper.fromRef(taskRef1) } returns task1
        coEvery { mapper.fromRef(taskRef2) } returns task2
        val result = datasource.getTasks().first()
        coVerify { database.child(TASKS_NODE) }
        coVerify { tasksNode.flowList(TaskRef::class.java) }
        coVerify { mapper.fromRef(taskRef1) }
        coVerify { mapper.fromRef(taskRef2) }
        assertEquals(tasks, result)
    }

    @Test
    fun `given saveTask, when task is provided, then save it to firebase database`() = runTest {
        coEvery { database.waitForConnection() } returns true
        coEvery { database.generateNodeId() } returns taskId
        coEvery { mapper.toRef(any()) } returns taskRef1
        coEvery { database.child(any()) } returns tasksNode
        coEvery { tasksNode.child(any()) } returns taskNode
        coEvery { taskNode.setValue(any()) } returns task
        coEvery { task.setValueListeners() } returns mockk()
        datasource.saveTask(task1)
        coVerify { database.waitForConnection() }
        coVerify { database.generateNodeId() }
        coVerify { mapper.toRef(task1) }
        coVerify { database.child(TASKS_NODE) }
        coVerify { tasksNode.child(taskId) }
        coVerify { taskNode.setValue(taskRef1) }
        coVerify { task.setValueListeners() }
    }

    @Test
    fun `given updateTask, when task is provided, then update it in firebase database`() = runTest {
        coEvery { database.waitForConnection() } returns true
        coEvery { mapper.toRef(any()) } returns taskRef1
        coEvery { database.child(any()) } returns tasksNode
        coEvery { tasksNode.child(any()) } returns taskNode
        coEvery { taskNode.setValue(any()) } returns task
        coEvery { task.setValueListeners() } returns mockk()
        datasource.updateTask(task1)
        coVerify { database.waitForConnection() }
        coVerify { mapper.toRef(task1) }
        coVerify { database.child(TASKS_NODE) }
        coVerify { tasksNode.child(taskId) }
        coVerify { taskNode.setValue(taskRef1) }
        coVerify { task.setValueListeners() }
    }

    @Test
    fun `given deleteTask, when id is provided, then delete task with provided id`() = runTest {
        coEvery { database.waitForConnection() } returns true
        coEvery { database.child(any()) } returns tasksNode
        coEvery { tasksNode.child(any()) } returns taskNode
        coEvery { taskNode.removeValue() } returns task
        coEvery { task.setValueListeners() } returns mockk()
        datasource.deleteTask(taskId)
        coVerify { database.waitForConnection()}
        coVerify { database.child(TASKS_NODE) }
        coVerify { tasksNode.child(taskId) }
        coVerify { taskNode.removeValue() }
        coVerify { task.setValueListeners() }
    }

    companion object {
        private const val taskId = "1"
        private val taskRef1 = TaskRef(taskId, "my task", false, 0L, 7, false)
        private val task1 = Tasc(taskId, "my task", false, 0L, 7, false)
        private val taskRef2 = TaskRef("2", "my task two", false, 0L, 7, false)
        private val task2 = Tasc("2", "my task two", false, 0L, 7, false)

        private const val TASKS_NODE = "tasks"
    }

}