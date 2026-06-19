package com.example.excerption.data.repository

import com.example.excerption.data.local.dao.ReviewDao
import com.example.excerption.data.local.entity.ReviewEntity

class ReviewRepository(private val reviewDao: ReviewDao) {
    fun observeReviews() = reviewDao.observeReviews()
    fun observeReviewsByBook(bookId: Long) = reviewDao.observeReviewsByBook(bookId)
    suspend fun getReview(id: Long) = reviewDao.getReview(id)
    suspend fun insertReview(review: ReviewEntity) = reviewDao.insertReview(review)
    suspend fun updateReview(review: ReviewEntity) = reviewDao.updateReview(review)
    suspend fun deleteReview(review: ReviewEntity) = reviewDao.deleteReview(review)
}
