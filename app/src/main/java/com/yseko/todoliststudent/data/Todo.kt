package com.yseko.todoliststudent.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="title")
    val todoTitle: String,
    @ColumnInfo(name="date")
    val todoDate: String,
    @ColumnInfo(name="category")
    val todoCategory: String
)

