package com.example.excerption.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.excerption.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY updatedAt DESC")
    fun observeReviews(): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY updatedAt DESC")
    fun observeReviewsByBook(bookId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getReview(id: Long): ReviewEntity?

    @Insert
    suspend fun insertReview(review: ReviewEntity): Long

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)
}
