package com.carles.lalloriguera.di

import android.content.Context
import androidx.room.Room
import com.carles.lalloriguera.AppDispatchers
import com.carles.lalloriguera.data.TaskDatasource
import com.carles.lalloriguera.data.local.TaskDao
import com.carles.lalloriguera.data.local.TaskDatabase
import com.carles.lalloriguera.data.local.TaskLocalDatasource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestModule {

    @Provides
    @Singleton
    fun provideDispatchers(): AppDispatchers {
        val main = Dispatchers.Main
        return AppDispatchers(main, main, main)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
            .allowMainThreadQueries()
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
        return Firebase.database("http://10.0.2.2:9000?ns=la-lloriguera").reference
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppBindModule::class]
)
abstract class TestBindModule {

    @Binds
    @Singleton
    abstract fun provideDatasource(datasource: TaskLocalDatasource): TaskDatasource
}