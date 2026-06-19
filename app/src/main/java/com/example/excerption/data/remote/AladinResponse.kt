package com.example.excerption.data.remote
import com.google.gson.annotations.SerializedName

data class AladinResponse(
    @SerializedName("item") val item: List<AladinItem>?
)

data class AladinItem(
    @SerializedName("title") val title: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("cover") val cover: String?
)