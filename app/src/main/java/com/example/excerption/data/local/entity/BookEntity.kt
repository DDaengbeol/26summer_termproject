package com.example.excerption.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val publisher: String? = null,
    val thumbnailUrl: String? = null,
    val isbn: String? = null,
    val categoryName: String? = null,
    val rating: Float = 0f,
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val oneLineReview: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
