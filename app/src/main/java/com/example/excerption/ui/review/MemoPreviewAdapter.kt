package com.example.excerption.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.data.local.entity.ReviewEntity

class MemoPreviewAdapter(
    private val onMemoClick: (ReviewEntity) -> Unit
) : RecyclerView.Adapter<MemoPreviewAdapter.MemoPreviewViewHolder>() {

    private val items = mutableListOf<ReviewEntity>()

    fun submitList(memos: List<ReviewEntity>) {
        items.clear()
        items.addAll(memos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoPreviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo_preview, parent, false)
        return MemoPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoPreviewViewHolder, position: Int) {
        holder.bind(items.getOrNull(position))
    }

    override fun getItemCount(): Int = if (items.isEmpty()) PLACEHOLDER_COUNT else items.size

    inner class MemoPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.memoPreviewContentTextView)

        fun bind(memo: ReviewEntity?) {
            contentTextView.text = memo?.content.orEmpty()
            itemView.isClickable = memo != null
            itemView.setOnClickListener {
                memo?.let(onMemoClick)
            }
        }
    }

    private companion object {
        const val PLACEHOLDER_COUNT = 4
    }
}
