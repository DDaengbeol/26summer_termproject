package com.example.excerption.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.loadCover
import com.example.excerption.data.local.entity.BookEntity

data class CalendarDay(
    val dayOfMonth: Int?,
    val books: List<BookEntity>
)

class BookCalendarAdapter(
    private val onBookClick: (BookEntity) -> Unit
) : RecyclerView.Adapter<BookCalendarAdapter.CalendarDayViewHolder>() {

    private val items = mutableListOf<CalendarDay>()

    fun submitList(days: List<CalendarDay>) {
        items.clear()
        items.addAll(days)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CalendarDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.calendarDayTextView)
        private val firstCoverImageView: ImageView = itemView.findViewById(R.id.calendarFirstCoverImageView)
        private val secondCoverImageView: ImageView = itemView.findViewById(R.id.calendarSecondCoverImageView)

        fun bind(day: CalendarDay) {
            val firstBook = day.books.getOrNull(0)
            dayTextView.text = day.dayOfMonth?.toString().orEmpty()
            dayTextView.isVisible = firstBook == null
            itemView.alpha = if (day.dayOfMonth == null) 0f else 1f
            itemView.isEnabled = day.dayOfMonth != null

            firstCoverImageView.isVisible = firstBook != null
            secondCoverImageView.isVisible = false

            firstBook?.let { book ->
                firstCoverImageView.loadCover(book.thumbnailUrl)
                firstCoverImageView.setOnClickListener { onBookClick(book) }
            }

            itemView.setOnClickListener {
                day.books.firstOrNull()?.let(onBookClick)
            }
        }
    }
}
