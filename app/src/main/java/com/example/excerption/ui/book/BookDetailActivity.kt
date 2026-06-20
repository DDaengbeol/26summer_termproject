package com.example.excerption.ui.book

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.common.loadCover
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.BookEntity
import com.example.excerption.ui.excerpt.ExcerptEditActivity
import com.example.excerption.ui.excerpt.ExcerptListActivity
import com.example.excerption.ui.excerpt.ExcerptPreviewAdapter
import com.example.excerption.ui.review.MemoListActivity
import com.example.excerption.ui.review.MemoPreviewAdapter
import com.example.excerption.ui.review.ReviewActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity() {
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    private var bookId: Long = -1
    private var currentBook: BookEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        if (bookId == -1L) {
            Toast.makeText(this, "책 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<ImageButton>(R.id.bookDetailBackButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.bookEditButton).setOnClickListener { showEditDialog() }
        findViewById<ImageButton>(R.id.bookDeleteButton).setOnClickListener { confirmDeleteBook() }
        findViewById<TextView>(R.id.memoSectionTitleTextView).setOnClickListener { openMemoList() }
        findViewById<TextView>(R.id.excerptSectionTitleTextView).setOnClickListener { openExcerptList() }

        bindBookInfo()
        bindLists()
    }

    override fun onResume() {
        super.onResume()
        if (bookId != -1L) bindBookInfo()
    }

    private fun bindBookInfo() {
        lifecycleScope.launch {
            val book = AppContainer.bookRepository.getBook(bookId)
            if (book == null) {
                Toast.makeText(this@BookDetailActivity, "책 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            currentBook = book
            renderBook(book)
        }
    }

    private fun renderBook(book: BookEntity) {
        findViewById<ImageView>(R.id.bookCoverImageView).loadCover(book.thumbnailUrl)
        findViewById<TextView>(R.id.bookTitleTextView).text = book.title
        findViewById<TextView>(R.id.bookAuthorTextView).text =
            listOf(book.author, book.publisher)
                .filter { !it.isNullOrBlank() }
                .joinToString(" · ")
        findViewById<RatingBar>(R.id.bookDetailRatingBar).rating = book.rating
        val oneLineReview = book.oneLineReview ?: "아직 한줄평이 없습니다."
        findViewById<TextView>(R.id.bookOneLineTextView).text = "“  $oneLineReview  ”"
        findViewById<TextView>(R.id.bookDateTextView).text =
            listOfNotNull(book.startedAt?.formatDate(), book.finishedAt?.formatDate())
                .joinToString(" - ")
                .ifBlank { "읽은 기간 미등록" }
    }

    private fun bindLists() {
        val memoAdapter = MemoPreviewAdapter { memo ->
            startActivity(
                Intent(this, ReviewActivity::class.java)
                    .putExtra(IntentKeys.BOOK_ID, bookId)
                    .putExtra(IntentKeys.REVIEW_ID, memo.id)
            )
        }
        val excerptAdapter = ExcerptPreviewAdapter { excerpt ->
            startActivity(
                Intent(this, ExcerptEditActivity::class.java)
                    .putExtra(IntentKeys.BOOK_ID, bookId)
                    .putExtra(IntentKeys.EXCERPT_ID, excerpt.id)
            )
        }

        findViewById<RecyclerView>(R.id.detailMemoRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@BookDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = memoAdapter
            isNestedScrollingEnabled = false
        }
        findViewById<RecyclerView>(R.id.detailExcerptRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@BookDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = excerptAdapter
            isNestedScrollingEnabled = false
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    AppContainer.reviewRepository.observeReviewsByBook(bookId).collect { memos ->
                        memoAdapter.submitList(memos.take(3))
                    }
                }
                launch {
                    AppContainer.excerptRepository.observeExcerptsByBook(bookId).collect { excerpts ->
                        excerptAdapter.submitList(excerpts.take(3))
                    }
                }
            }
        }
    }

    private fun showEditDialog() {
        val book = currentBook ?: return
        var startedAt = book.startedAt
        var finishedAt = book.finishedAt

        val dialog = BottomSheetDialog(this)
        val sheetParent = findViewById<ViewGroup>(android.R.id.content)
        val sheetView = layoutInflater.inflate(R.layout.dialog_book_edit, sheetParent, false)
        dialog.setContentView(sheetView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val closeButton = sheetView.findViewById<TextView>(R.id.editSheetCloseButton)
        val ratingBar = sheetView.findViewById<RatingBar>(R.id.editSheetRatingBar)
        val startButton = sheetView.findViewById<Button>(R.id.editSheetStartDateButton)
        val finishButton = sheetView.findViewById<Button>(R.id.editSheetFinishDateButton)
        val reviewCountTextView = sheetView.findViewById<TextView>(R.id.editSheetReviewCountTextView)
        val oneLineEditText = sheetView.findViewById<EditText>(R.id.editSheetOneLineEditText)
        val saveButton = sheetView.findViewById<Button>(R.id.editSheetSaveButton)

        fun updateDateButtons() {
            startButton.text = startedAt?.formatDate() ?: "시작일"
            finishButton.text = finishedAt?.formatDate() ?: "종료일"
        }

        fun updateReviewCount() {
            reviewCountTextView.text = "(${oneLineEditText.text.length}/500)"
        }

        ratingBar.numStars = 5
        ratingBar.setMax(5)
        ratingBar.stepSize = 1f
        ratingBar.rating = book.rating.coerceIn(0f, 5f)
        ratingBar.setIsIndicator(false)
        oneLineEditText.setText(book.oneLineReview.orEmpty())
        updateDateButtons()
        updateReviewCount()

        oneLineEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateReviewCount()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        closeButton.setOnClickListener { dialog.dismiss() }
        startButton.setOnClickListener {
            pickDate(startedAt) { selected ->
                startedAt = selected
                updateDateButtons()
            }
        }
        finishButton.setOnClickListener {
            pickDate(finishedAt) { selected ->
                finishedAt = selected
                updateDateButtons()
            }
        }
        saveButton.setOnClickListener {
            lifecycleScope.launch {
                val updated = book.copy(
                    rating = ratingBar.rating,
                    startedAt = startedAt,
                    finishedAt = finishedAt,
                    oneLineReview = oneLineEditText.text.toString().trim().ifBlank { null }
                )
                AppContainer.bookRepository.updateBook(updated)
                currentBook = updated
                renderBook(updated)
                dialog.dismiss()
            }
        }

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
            bottomSheet?.layoutParams?.width = FrameLayout.LayoutParams.MATCH_PARENT
            bottomSheet?.let {
                BottomSheetBehavior.from(it).apply {
                    skipCollapsed = true
                    state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        dialog.show()
    }

    private fun confirmDeleteBook() {
        val book = currentBook ?: return
        AlertDialog.Builder(this)
            .setTitle("책 삭제")
            .setMessage("서재에서 '${book.title}'을 삭제할까요?")
            .setNegativeButton("취소", null)
            .setPositiveButton("삭제") { _, _ ->
                lifecycleScope.launch {
                    AppContainer.bookRepository.deleteBook(book)
                    finish()
                }
            }
            .show()
    }

    private fun openMemoList() {
        startActivity(
            Intent(this, MemoListActivity::class.java)
                .putExtra(IntentKeys.BOOK_ID, bookId)
        )
    }

    private fun openExcerptList() {
        startActivity(
            Intent(this, ExcerptListActivity::class.java)
                .putExtra(IntentKeys.BOOK_ID, bookId)
        )
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

    private fun Long.formatDate(): String = dateFormat.format(Date(this))
}
