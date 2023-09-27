package com.carles.lalloriguera

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.carles.lalloriguera.ui.worker.CheckDelayedTasksWorkerExecutor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var checkDelayedTasksWorkerExecutor: CheckDelayedTasksWorkerExecutor

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(Log.DEBUG)
        .build()

    override fun onCreate() {
        super.onCreate()
        checkDelayedTasksWorkerExecutor.enqueueWork()
    }
}