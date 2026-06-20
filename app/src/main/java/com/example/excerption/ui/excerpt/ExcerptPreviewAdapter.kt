package com.example.excerption.ui.excerpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.data.local.entity.ExcerptEntity

class ExcerptPreviewAdapter(
    private val onExcerptClick: (ExcerptEntity) -> Unit
) : RecyclerView.Adapter<ExcerptPreviewAdapter.ExcerptPreviewViewHolder>() {

    private val items = mutableListOf<ExcerptEntity>()

    fun submitList(excerpts: List<ExcerptEntity>) {
        items.clear()
        items.addAll(excerpts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcerptPreviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_excerpt_preview_small, parent, false)
        return ExcerptPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExcerptPreviewViewHolder, position: Int) {
        holder.bind(items.getOrNull(position))
    }

    override fun getItemCount(): Int = if (items.isEmpty()) PLACEHOLDER_COUNT else items.size

    inner class ExcerptPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.excerptPreviewContentTextView)

        fun bind(excerpt: ExcerptEntity?) {
            contentTextView.text = excerpt?.content.orEmpty()
            itemView.isClickable = excerpt != null
            itemView.setOnClickListener {
                excerpt?.let(onExcerptClick)
            }
        }
    }

    private companion object {
        const val PLACEHOLDER_COUNT = 4
    }
}
