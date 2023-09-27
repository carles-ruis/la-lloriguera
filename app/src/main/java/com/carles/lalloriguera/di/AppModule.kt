package com.carles.lalloriguera.di

import android.content.Context
import androidx.room.Room
import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskDatasource
import com.carles.lalloriguera.data.local.TaskDao
import com.carles.lalloriguera.data.local.TaskDatabase
import com.carles.lalloriguera.data.remote.TaskRemoteDatasource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatchers(): AppDispatchers {
        return AppDispatchers(
            io = Dispatchers.IO,
            ui = Dispatchers.Main,
            default = Dispatchers.Default
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        // URL for testing with an emulator
        return Firebase.database.reference
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule() {

    @Binds
    @Singleton
    // abstract fun provideDatasource(datasource: TaskLocalDatasource): TaskDatasource
    abstract fun provideDatasource(datasource: TaskRemoteDatasource): TaskDatasource
}

