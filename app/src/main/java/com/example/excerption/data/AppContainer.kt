package com.example.excerption.data

import android.content.Context
import com.example.excerption.data.local.AppDatabase
import com.example.excerption.data.remote.AladinService
import com.example.excerption.data.repository.BookRepository
import com.example.excerption.data.repository.ExcerptRepository
import com.example.excerption.data.repository.ReviewRepository
import com.example.excerption.data.repository.UserRepository // 1. import 추가
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {
    lateinit var aladinService: AladinService
    lateinit var bookRepository: BookRepository
    lateinit var excerptRepository: ExcerptRepository
    lateinit var reviewRepository: ReviewRepository
    lateinit var userRepository: UserRepository // 2. 변수 선언

    fun init(context: Context) {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.aladin.co.kr/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        aladinService = retrofit.create(AladinService::class.java)

        val db = AppDatabase.getInstance(context)

        // 3. 레포지토리 초기화
        bookRepository = BookRepository(db.bookDao(), aladinService)
        excerptRepository = ExcerptRepository(db.excerptDao())
        reviewRepository = ReviewRepository(db.reviewDao())
        userRepository = UserRepository(db.userDao()) // 4. DAO와 연결하여 초기화
    }
}