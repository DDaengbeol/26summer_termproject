package com.example.excerption.data.repository

import com.example.excerption.data.local.dao.ExcerptDao
import com.example.excerption.data.local.entity.ExcerptEntity

class ExcerptRepository(private val excerptDao: ExcerptDao) {
    fun observeAllExcerpts() = excerptDao.observeAllExcerpts()
    fun observeExcerptsByBook(bookId: Long) = excerptDao.observeExcerptsByBook(bookId)
    suspend fun getExcerpt(id: Long) = excerptDao.getExcerpt(id)
    suspend fun insertExcerpt(excerpt: ExcerptEntity) = excerptDao.insertExcerpt(excerpt)
    suspend fun updateExcerpt(excerpt: ExcerptEntity) = excerptDao.updateExcerpt(excerpt)
    suspend fun deleteExcerpt(excerpt: ExcerptEntity) = excerptDao.deleteExcerpt(excerpt)
}
