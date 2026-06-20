package com.example.excerption.data.remote

data class OnlineBook(
    val title: String,
    val author: String,
    val publisher: String?,
    val isbn: String?,
    val categoryName: String?,
    val coverUrl: String?,
    val link: String?
)
