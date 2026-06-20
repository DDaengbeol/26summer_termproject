package com.example.excerption.data.repository

import com.example.excerption.data.local.dao.BookDao
import com.example.excerption.data.local.entity.BookEntity

class BookRepository(private val bookDao: BookDao) {
    fun observeBooks() = bookDao.observeBooks()
    fun observeBooksCreatedBetween(startInclusive: Long, endExclusive: Long) =
        bookDao.observeBooksCreatedBetween(startInclusive, endExclusive)
    suspend fun getBook(id: Long) = bookDao.getBook(id)
    suspend fun insertBook(book: BookEntity) = bookDao.insertBook(book)
    suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)
    suspend fun deleteBook(book: BookEntity) = bookDao.deleteBook(book)
}
