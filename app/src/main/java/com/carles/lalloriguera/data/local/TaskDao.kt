package com.carles.lalloriguera.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("select * from task where _id=:id")
    suspend fun loadTask(id: Int): TaskEntity


    @Query("select * from task")
    fun loadTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity): Int

    @Query("delete from task where _id=:id")
    suspend fun deleteTask(id: Int): Int

}