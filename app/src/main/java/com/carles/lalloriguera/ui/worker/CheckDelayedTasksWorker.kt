package com.carles.lalloriguera.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.carles.lalloriguera.common.NotificationHelper
import com.carles.lalloriguera.domain.GetTasks
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class CheckDelayedTasksWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getTasks: GetTasks,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    private val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    private fun createDelayedTasksNotification(tasks: List<String>) {
        notificationHelper.createDelayedTasksChannel()
        notificationHelper.createDelayedTasksNotification(tasks)
    }

    override suspend fun doWork(): Result = getTasks.execute()
        .catch { error ->
            Log.w("CheckDelayedTasksWorker", error.localizedMessage ?: "getTasks error")
            Result.failure()
        }
        .first()
        .let { tasks ->
            Log.i("CheckDelayedTasksWorker", "executed at ${formatter.format(Date())}")

            val delayedTasks: List<String> = tasks.filter { it.daysRemaining < 0 }.map { task -> task.name }
            if (delayedTasks.isNotEmpty()) {
                createDelayedTasksNotification(delayedTasks)
            }
            Result.success()
        }
}