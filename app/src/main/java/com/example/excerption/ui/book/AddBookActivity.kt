package com.example.excerption.ui.book

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.common.loadCover
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.BookEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

class AddBookActivity : AppCompatActivity() {
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    private var startedAt: Long? = null
    private var finishedAt: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        val title = intent.getStringExtra(IntentKeys.BOOK_TITLE).orEmpty()
        val author = intent.getStringExtra(IntentKeys.BOOK_AUTHOR).orEmpty()
        val publisher = intent.getStringExtra(IntentKeys.BOOK_PUBLISHER)
        val isbn = intent.getStringExtra(IntentKeys.BOOK_ISBN)
        val categoryName = intent.getStringExtra(IntentKeys.BOOK_CATEGORY)
        val coverUrl = intent.getStringExtra(IntentKeys.BOOK_COVER_URL)

        if (title.isBlank()) {
            Toast.makeText(this, "검색한 책 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<ImageButton>(R.id.addBookBackButton).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.addBookCoverImageView).loadCover(coverUrl)
        findViewById<TextView>(R.id.addBookTitleTextView).text = title
        findViewById<TextView>(R.id.addBookMetaTextView).text =
            listOf(author, publisher, categoryName)
                .filter { !it.isNullOrBlank() }
                .joinToString(" · ")

        val startDateButton = findViewById<Button>(R.id.startDateButton)
        val finishDateButton = findViewById<Button>(R.id.finishDateButton)
        val ratingBar = findViewById<RatingBar>(R.id.addBookRatingBar)
        val oneLineTextView = findViewById<TextView>(R.id.oneLineReviewEditText)

        startDateButton.setOnClickListener {
            pickDate(startedAt) { selected ->
                startedAt = selected
                startDateButton.text = dateFormat.format(selected)
            }
        }

        finishDateButton.setOnClickListener {
            pickDate(finishedAt) { selected ->
                finishedAt = selected
                finishDateButton.text = dateFormat.format(selected)
            }
        }

        findViewById<Button>(R.id.saveBookButton).setOnClickListener {
            lifecycleScope.launch {
                AppContainer.bookRepository.insertBook(
                    BookEntity(
                        title = title,
                        author = author,
                        publisher = publisher,
                        thumbnailUrl = coverUrl,
                        isbn = isbn,
                        categoryName = categoryName,
                        rating = ratingBar.rating,
                        startedAt = startedAt,
                        finishedAt = finishedAt,
                        oneLineReview = oneLineTextView.text.toString().trim().ifBlank { null }
                    )
                )
                Toast.makeText(this@AddBookActivity, "책을 등록했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun pickDate(currentValue: Long?, onSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance().apply {
            currentValue?.let { timeInMillis = it }
        }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onSelected(selected.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
