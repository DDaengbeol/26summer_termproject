package com.example.excerption.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.data.local.entity.ReviewEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewAdapter(
    private val onReviewClick: (ReviewEntity) -> Unit,
    private val onReviewDelete: ((ReviewEntity) -> Unit)? = null
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    private val items = mutableListOf<ReviewEntity>()

    fun submitList(reviews: List<ReviewEntity>) {
        items.clear()
        items.addAll(reviews)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.itemReviewContentTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.itemReviewDateTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.itemReviewDeleteButton)

        fun bind(review: ReviewEntity) {
            contentTextView.text = review.content
            dateTextView.text = dateFormat.format(Date(review.updatedAt))
            itemView.setOnClickListener { onReviewClick(review) }
            deleteButton.isVisible = false
            deleteButton.setOnClickListener { onReviewDelete?.invoke(review) }
        }
    }
}
