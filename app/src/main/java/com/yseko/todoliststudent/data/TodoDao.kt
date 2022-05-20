package com.yseko.todoliststudent.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT title, date from todo WHERE category = :category ORDER BY date ASC")
    fun getTodoByCategory(category: String): Flow<List<Todo>>

    @Query("SELECT title, date from todo WHERE id = :id")
    fun getTodoById(id: Int): Flow<Todo>

    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}