package com.example.excerption.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.data.local.entity.ReviewEntity

class ReviewAdapter(
    private val onReviewClick: (ReviewEntity) -> Unit,
    private val onDeleteClick: (ReviewEntity) -> Unit // 삭제 콜백 추가
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

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
        private val ratingTextView: TextView = itemView.findViewById(R.id.itemReviewRatingTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.itemReviewContentTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.btnDeleteReview) // 삭제 버튼 ID 연결

        fun bind(review: ReviewEntity) {
            ratingTextView.text = "별점 ${review.rating}"
            contentTextView.text = review.content

            // 클릭 리스너 연결
            itemView.setOnClickListener { onReviewClick(review) }
            deleteButton.setOnClickListener { onDeleteClick(review) }
        }
    }
}