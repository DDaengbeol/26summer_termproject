package com.example.excerption.data

import android.content.Context
import com.example.excerption.data.local.AppDatabase
import com.example.excerption.data.repository.BookRepository
import com.example.excerption.data.repository.ExcerptRepository
import com.example.excerption.data.repository.ReviewRepository

object AppContainer {
    lateinit var database: AppDatabase
        private set

    lateinit var bookRepository: BookRepository
        private set

    lateinit var excerptRepository: ExcerptRepository
        private set

    lateinit var reviewRepository: ReviewRepository
        private set

    fun init(context: Context) {
        database = AppDatabase.getInstance(context.applicationContext)
        bookRepository = BookRepository(database.bookDao())
        excerptRepository = ExcerptRepository(database.excerptDao())
        reviewRepository = ReviewRepository(database.reviewDao())
    }
}
