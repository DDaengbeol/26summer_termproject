package com.example.excerption.ui.book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.loadCover
import com.example.excerption.data.remote.OnlineBook

class BookSearchAdapter(
    private val onBookClick: (OnlineBook) -> Unit
) : RecyclerView.Adapter<BookSearchAdapter.BookSearchViewHolder>() {

    private val items = mutableListOf<OnlineBook>()

    fun submitList(books: List<OnlineBook>) {
        items.clear()
        items.addAll(books)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_search, parent, false)
        return BookSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookSearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class BookSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.searchBookCoverImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.searchBookTitleTextView)
        private val metaTextView: TextView = itemView.findViewById(R.id.searchBookMetaTextView)

        fun bind(book: OnlineBook) {
            coverImageView.loadCover(book.coverUrl)
            titleTextView.text = book.title
            metaTextView.text = listOf(book.author, book.publisher, book.categoryName)
                .filter { !it.isNullOrBlank() }
                .joinToString(" · ")
            itemView.setOnClickListener { onBookClick(book) }
        }
    }
}
