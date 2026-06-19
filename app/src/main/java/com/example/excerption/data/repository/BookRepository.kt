package com.example.excerption.data.repository

import com.example.excerption.data.local.dao.BookDao
import com.example.excerption.data.local.entity.BookEntity
import com.example.excerption.data.remote.AladinService
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao, private val aladinService: AladinService) {

    // 데이터 변경을 관찰할 수 있는 Flow
    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooksFlow()

    suspend fun getBook(bookId: Long): BookEntity? = bookDao.getBookById(bookId)

    suspend fun insertBook(book: BookEntity) = bookDao.insertBook(book)

    suspend fun fetchAndSaveAladinBooks(query: String) {
        try {
            val response = aladinService.searchBooks(ttbkey = "ttbkch50600016001", query = query)
            response.item?.forEach { item ->
                bookDao.insertBook(BookEntity(
                    title = item.title ?: "제목 없음",
                    author = item.author ?: "저자 미상",
                    cover = item.cover ?: ""
                ))
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}