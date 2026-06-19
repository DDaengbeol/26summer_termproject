package com.example.excerption.ui.book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.excerption.R
import com.example.excerption.data.local.entity.BookEntity

// 1. 클릭 시 실행할 동작을 파라미터로 받습니다 (book: BookEntity) -> Unit
class BookAdapter(
    private val onItemClick: (BookEntity) -> Unit
) : ListAdapter<BookEntity, BookAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View, private val onItemClick: (BookEntity) -> Unit) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.itemBookTitleTextView)
        private val author = view.findViewById<TextView>(R.id.itemBookAuthorTextView)
        private val coverImage = view.findViewById<ImageView>(R.id.itemBookImageView)

        fun bind(book: BookEntity) {
            title.text = book.title
            author.text = book.author

            Glide.with(coverImage.context)
                .load(book.cover)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(coverImage)

            // 2. 아이템 클릭 시 onItemClick 호출
            itemView.setOnClickListener {
                onItemClick(book)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BookEntity>() {
        override fun areItemsTheSame(o: BookEntity, n: BookEntity) = o.id == n.id
        override fun areContentsTheSame(o: BookEntity, n: BookEntity) = o == n
    }
}