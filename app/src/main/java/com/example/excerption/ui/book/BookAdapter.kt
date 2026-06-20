package com.example.excerption.ui.book

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.loadCover
import com.example.excerption.data.local.entity.BookEntity
import kotlin.math.roundToInt

class BookAdapter(
    private val onBookClick: (BookEntity) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val items = mutableListOf<BookEntity>()

    fun submitList(books: List<BookEntity>) {
        items.clear()
        items.addAll(books)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.itemBookCoverImageView)
        private val ratingTextView: TextView = itemView.findViewById(R.id.itemBookRatingTextView)

        fun bind(book: BookEntity) {
            coverImageView.loadCover(book.thumbnailUrl)
            ratingTextView.text = book.rating.toStars()
            itemView.setOnClickListener { onBookClick(book) }
        }
    }

    private fun Float.toStars(): SpannableString {
        val filledStars = roundToInt().coerceIn(0, 5)
        return SpannableString("★★★★★").apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#FFCC00")),
                0,
                filledStars,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(Color.parseColor("#E2E2E2")),
                filledStars,
                5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}
