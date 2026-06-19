package com.example.excerption.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_table")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val author: String,
    val cover: String = ""
)