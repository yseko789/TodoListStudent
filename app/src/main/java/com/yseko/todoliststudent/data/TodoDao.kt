package com.yseko.todoliststudent.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
//    @Query("SELECT title, date from todo WHERE category = :category ORDER BY date ASC")
//    fun getTodoByCategory(category: String): Flow<List<Todo>>
//
    @Query("SELECT categoryName from category")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * from todo WHERE todoId = :id")
    fun getTodoById(id: Int): Flow<Todo>

    @Query("SELECT * from todo ORDER BY date ASC")
    fun getTodoAll(): Flow<List<Todo>>

    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}