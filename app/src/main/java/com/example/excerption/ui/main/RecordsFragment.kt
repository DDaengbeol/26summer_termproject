package com.example.excerption.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.BookEntity
import com.example.excerption.ui.book.BookDetailActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

class RecordsFragment : Fragment() {
    private val monthTitleFormat = SimpleDateFormat("yy년 M월", Locale.KOREA)
    private val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val visibleMonth = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private lateinit var statsTabButton: Button
    private lateinit var calendarTabButton: Button
    private lateinit var statsContainer: ScrollView
    private lateinit var calendarContainer: LinearLayout
    private lateinit var categoryPieChartView: PieChartView
    private lateinit var monthlyChartView: BarChartView
    private lateinit var ratingDistributionView: RatingDistributionView
    private lateinit var monthTitleTextView: TextView
    private lateinit var calendarAdapter: BookCalendarAdapter
    private var allBooks = emptyList<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statsTabButton = view.findViewById(R.id.recordsStatsTabButton)
        calendarTabButton = view.findViewById(R.id.recordsCalendarTabButton)
        statsContainer = view.findViewById(R.id.recordsStatsContainer)
        calendarContainer = view.findViewById(R.id.recordsCalendarContainer)
        categoryPieChartView = view.findViewById(R.id.categoryPieChartView)
        monthlyChartView = view.findViewById(R.id.monthlyChartView)
        ratingDistributionView = view.findViewById(R.id.ratingDistributionView)
        monthTitleTextView = view.findViewById(R.id.recordsCalendarMonthTextView)

        calendarAdapter = BookCalendarAdapter { book ->
            startActivity(
                Intent(requireContext(), BookDetailActivity::class.java)
                    .putExtra(IntentKeys.BOOK_ID, book.id)
            )
        }

        view.findViewById<RecyclerView>(R.id.recordsBookCalendarRecyclerView).apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
        }

        statsTabButton.setOnClickListener { showStats() }
        calendarTabButton.setOnClickListener { showCalendar() }
        view.findViewById<ImageButton>(R.id.recordsPreviousMonthButton).setOnClickListener {
            visibleMonth.add(Calendar.MONTH, -1)
            renderCalendar()
        }
        view.findViewById<ImageButton>(R.id.recordsNextMonthButton).setOnClickListener {
            visibleMonth.add(Calendar.MONTH, 1)
            renderCalendar()
        }

        showStats()
        observeBooks()
    }

    private fun observeBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppContainer.bookRepository.observeBooks().collect { books ->
                    allBooks = books
                    renderStats()
                    renderCalendar()
                }
            }
        }
    }

    private fun showStats() {
        statsContainer.isVisible = true
        calendarContainer.isVisible = false
        statsTabButton.isSelected = true
        calendarTabButton.isSelected = false
    }

    private fun showCalendar() {
        statsContainer.isVisible = false
        calendarContainer.isVisible = true
        statsTabButton.isSelected = false
        calendarTabButton.isSelected = true
    }

    private fun renderStats() {
        val categoryEntries = allBooks
            .groupingBy { it.categoryLabel() }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value.toFloat() }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val monthlyEntries = (0..11).map { month ->
            val count = allBooks.count { book ->
                val finishedAt = book.finishedAt ?: return@count false
                Calendar.getInstance().apply {
                    timeInMillis = finishedAt
                }.let { calendar ->
                    calendar.get(Calendar.YEAR) == currentYear &&
                        calendar.get(Calendar.MONTH) == month
                }
            }
            "${month + 1}월" to count.toFloat()
        }

        val ratingEntries = (1..5).map { rating ->
            "$rating" to allBooks.count { book ->
                book.rating.roundToInt().coerceIn(0, 5) == rating
            }.toFloat()
        }

        categoryPieChartView.submitEntries(categoryEntries)
        monthlyChartView.submitEntries(monthlyEntries)
        ratingDistributionView.submitEntries(ratingEntries)
    }

    private fun renderCalendar() {
        monthTitleTextView.text = monthTitleFormat.format(visibleMonth.time)
        val firstDay = Calendar.getInstance().apply { timeInMillis = visibleMonth.timeInMillis }
        val daysInMonth = firstDay.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstWeekdayOffset = firstDay.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        val start = visibleMonth.timeInMillis
        val end = Calendar.getInstance().apply {
            timeInMillis = visibleMonth.timeInMillis
            add(Calendar.MONTH, 1)
        }.timeInMillis
        val visibleBooks = allBooks.filter { book ->
            val finishedAt = book.finishedAt ?: return@filter false
            finishedAt in start until end
        }
        val booksByDate = visibleBooks.groupBy { book ->
            dateKeyFormat.format(Date(book.finishedAt ?: 0L))
        }

        val days = buildList {
            repeat(firstWeekdayOffset) { add(CalendarDay(dayOfMonth = null, books = emptyList())) }
            for (day in 1..daysInMonth) {
                val date = Calendar.getInstance().apply {
                    timeInMillis = visibleMonth.timeInMillis
                    set(Calendar.DAY_OF_MONTH, day)
                }
                add(
                    CalendarDay(
                        dayOfMonth = day,
                        books = booksByDate[dateKeyFormat.format(date.time)].orEmpty()
                    )
                )
            }
        }
        calendarAdapter.submitList(days)
    }

    private fun BookEntity.categoryLabel(): String {
        val categories = categoryName
            ?.split(">")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()
        val subject = categories
            .dropWhile { it in broadCategoryLabels }
            .firstOrNull()
            ?: categories.firstOrNull()
            ?: return "미분류"

        return when {
            subject.contains("인문") || subject.contains("교양") -> "인문"
            subject.contains("철학") -> "철학"
            subject.contains("소설") || subject.contains("시/희곡") ||
                subject.contains("시에세이") || subject.contains("시집") ||
                subject.contains("에세이") || subject == "문학" -> "문학"
            subject.contains("역사") || subject.contains("문화") -> "역사"
            subject.contains("예술") || subject.contains("대중문화") || subject.contains("음악") ||
                subject.contains("미술") -> "예술"
            subject.contains("사회") || subject.contains("정치") || subject.contains("경제") ||
                subject.contains("경영") -> "사회"
            subject.contains("과학") || subject.contains("공학") || subject.contains("컴퓨터") ||
                subject.contains("IT") -> "과학"
            subject.contains("자기계발") -> "자기계발"
            subject.contains("어린이") || subject.contains("청소년") -> "어린이"
            else -> subject.substringBefore("/")
        }
    }

    private companion object {
        val broadCategoryLabels = setOf("국내도서", "외국도서", "eBook", "온라인중고", "음반", "DVD")
    }
}
