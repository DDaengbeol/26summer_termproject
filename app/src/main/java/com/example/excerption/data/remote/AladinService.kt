package com.example.excerption.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface AladinService {
    // 이 함수가 없어서 Repository에서 에러가 났던 것입니다.
    @GET("ttb/api/ItemSearch.aspx") // 검색 API 경로
    suspend fun searchBooks(
        @Query("ttbkey") ttbkey: String,
        @Query("Query") query: String,
        @Query("QueryType") queryType: String = "Title",
        @Query("MaxResults") maxResults: Int = 10,
        @Query("output") output: String = "JS",
        @Query("Version") version: String = "20131101"
    ): AladinResponse
}