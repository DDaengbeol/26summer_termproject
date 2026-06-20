package com.example.excerption.data.remote

import android.net.Uri
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AladinBookApi(private val ttbKey: String) {
    suspend fun search(query: String): List<OnlineBook> = withContext(Dispatchers.IO) {
        require(ttbKey.isNotBlank() && ttbKey != "YOUR_ALADIN_TTB_KEY") {
            "알라딘 TTBKey를 strings.xml의 aladin_ttb_key에 등록해 주세요."
        }

        val requestUrl = Uri.parse("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
            .buildUpon()
            .appendQueryParameter("ttbkey", ttbKey)
            .appendQueryParameter("Query", query)
            .appendQueryParameter("QueryType", "Keyword")
            .appendQueryParameter("MaxResults", "30")
            .appendQueryParameter("start", "1")
            .appendQueryParameter("SearchTarget", "Book")
            .appendQueryParameter("Cover", "Big")
            .appendQueryParameter("output", "js")
            .appendQueryParameter("Version", "20131101")
            .build()
            .toString()

        val connection = (URI(requestUrl).toURL().openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json,text/javascript,*/*")
            setRequestProperty("Accept-Charset", "UTF-8")
        }

        val response = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        val trimmedResponse = response.trim()
        val jsonText = if (trimmedResponse.startsWith("{")) {
            trimmedResponse
        } else {
            trimmedResponse.substringAfter("{", "")
                .substringBeforeLast("}", "")
                .let { "{$it}" }
        }
        val root = JSONObject(jsonText)
        val items = root.optJSONArray("item") ?: return@withContext emptyList()

        buildList {
            for (index in 0 until items.length()) {
                val item = items.optJSONObject(index) ?: continue
                add(
                    OnlineBook(
                        title = item.optString("title").orEmpty(),
                        author = item.optString("author").orEmpty(),
                        publisher = item.optString("publisher").ifBlank { null },
                        isbn = item.optString("isbn13").ifBlank {
                            item.optString("isbn").ifBlank { null }
                        },
                        categoryName = item.optString("categoryName").ifBlank { null },
                        coverUrl = item.optString("cover").ifBlank { null },
                        link = item.optString("link").ifBlank { null }
                    )
                )
            }
        }
    }
}
