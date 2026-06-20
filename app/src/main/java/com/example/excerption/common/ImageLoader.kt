package com.example.excerption.common

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.example.excerption.R
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ImageView.loadCover(
    url: String?,
    @DrawableRes placeholder: Int = R.drawable.bg_book_cover_placeholder
) {
    setImageResource(placeholder)
    tag = url
    if (url.isNullOrBlank()) return

    CoroutineScope(Dispatchers.IO).launch {
        val bitmap = runCatching {
            URL(url).openStream().use { BitmapFactory.decodeStream(it) }
        }.getOrNull()

        withContext(Dispatchers.Main) {
            if (tag == url && bitmap != null) {
                setImageBitmap(bitmap)
            }
        }
    }
}
