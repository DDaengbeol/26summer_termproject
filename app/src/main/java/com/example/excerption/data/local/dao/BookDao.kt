package com.example.excerption.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.excerption.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow // 이 import가 반드시 필요합니다!

@Dao
interface BookDao {
    @Query("SELECT * FROM book_table")
    fun getAllBooksFlow(): Flow<List<BookEntity>> // Flow로 변경

    @Query("SELECT * FROM book_table WHERE id = :id")
    suspend fun getBookById(id: Long): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)
}