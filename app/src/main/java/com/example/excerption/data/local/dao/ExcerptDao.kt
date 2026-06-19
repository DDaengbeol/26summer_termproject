package com.example.excerption.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.excerption.data.local.entity.ExcerptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcerptDao {
    @Query("SELECT * FROM excerpts ORDER BY createdAt DESC")
    fun observeAllExcerpts(): Flow<List<ExcerptEntity>>

    @Query("SELECT * FROM excerpts WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun observeExcerptsByBook(bookId: Long): Flow<List<ExcerptEntity>>

    @Query("SELECT * FROM excerpts WHERE id = :id")
    suspend fun getExcerpt(id: Long): ExcerptEntity?

    @Insert
    suspend fun insertExcerpt(excerpt: ExcerptEntity): Long

    @Update
    suspend fun updateExcerpt(excerpt: ExcerptEntity)

    @Delete
    suspend fun deleteExcerpt(excerpt: ExcerptEntity)
}
