package com.example.excerption.ui.excerpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.data.local.entity.ExcerptEntity

class ExcerptAdapter(
    private val onExcerptClick: (ExcerptEntity) -> Unit,
    private val onExcerptDelete: ((ExcerptEntity) -> Unit)? = null
) : RecyclerView.Adapter<ExcerptAdapter.ExcerptViewHolder>() {

    private val items = mutableListOf<ExcerptEntity>()

    fun submitList(excerpts: List<ExcerptEntity>) {
        items.clear()
        items.addAll(excerpts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcerptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_excerpt, parent, false)
        return ExcerptViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExcerptViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ExcerptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.itemExcerptContentTextView)
        private val pageTextView: TextView = itemView.findViewById(R.id.itemExcerptPageTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.itemExcerptDeleteButton)

        fun bind(excerpt: ExcerptEntity) {
            contentTextView.text = excerpt.content
            pageTextView.text = excerpt.page?.let { "${it}쪽" }.orEmpty()
            deleteButton.visibility = View.GONE
            deleteButton.setOnClickListener { onExcerptDelete?.invoke(excerpt) }
            itemView.setOnClickListener { onExcerptClick(excerpt) }
        }
    }
}
