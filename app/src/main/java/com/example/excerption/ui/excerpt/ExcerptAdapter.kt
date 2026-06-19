package com.example.excerption.ui.excerpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.data.local.entity.ExcerptEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExcerptAdapter(
    private val onExcerptClick: (ExcerptEntity) -> Unit,
    private val onDeleteClick: (ExcerptEntity) -> Unit
) : RecyclerView.Adapter<ExcerptAdapter.ExcerptViewHolder>() {

    private val items = mutableListOf<ExcerptEntity>()
    private val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    fun submitList(excerpts: List<ExcerptEntity>) {
        items.clear()
        items.addAll(excerpts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcerptViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_excerpt, parent, false)
        return ExcerptViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExcerptViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ExcerptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.itemExcerptContentTextView)
        private val metaTextView: TextView = itemView.findViewById(R.id.itemExcerptMetaTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteExcerptButton)

        fun bind(excerpt: ExcerptEntity) {
            contentTextView.text = excerpt.content

            // 페이지가 0보다 클 때만 "X쪽 · " 문구 표시
            val pageText = if ((excerpt.page ?: 0) > 0) "${excerpt.page}쪽 · " else ""
            metaTextView.text = pageText + formatter.format(Date(excerpt.createdAt))

            itemView.setOnClickListener { onExcerptClick(excerpt) }
            deleteButton.setOnClickListener { onDeleteClick(excerpt) }
        }
    }
}