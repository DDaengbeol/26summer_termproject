package com.example.excerption.ui.book

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.ui.excerpt.AddExcerptActivity
import com.example.excerption.ui.excerpt.ExcerptAdapter
import com.example.excerption.ui.excerpt.ExcerptListActivity
import com.example.excerption.ui.review.ReviewActivity
import com.example.excerption.ui.review.ReviewAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity() {
    private var bookId: Long = -1
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var excerptAdapter: ExcerptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // 1. 데이터 받기
        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        val title = intent.getStringExtra("BOOK_TITLE")
        val author = intent.getStringExtra("BOOK_AUTHOR")

        // 2. 뷰 연결
        val titleView = findViewById<TextView>(R.id.bookTitleTextView)
        val authorView = findViewById<TextView>(R.id.bookAuthorTextView)
        val reviewRecyclerView = findViewById<RecyclerView>(R.id.detailReviewRecyclerView)
        val excerptRecyclerView = findViewById<RecyclerView>(R.id.detailExcerptRecyclerView)

        titleView.text = title
        authorView.text = author

        // 3. 어댑터 초기화 및 연결
        reviewAdapter = ReviewAdapter(
            onReviewClick = { },
            onDeleteClick = { review ->
                lifecycleScope.launch { AppContainer.reviewRepository.deleteReview(review) }
            }
        )
        reviewRecyclerView.adapter = reviewAdapter
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)

        // [수정 완료] 발췌 삭제 기능 연결
        excerptAdapter = ExcerptAdapter(
            onExcerptClick = { /* 상세 보기 등의 동작 */ },
            onDeleteClick = { excerpt ->
                lifecycleScope.launch {
                    AppContainer.excerptRepository.deleteExcerpt(excerpt)
                }
            }
        )
        excerptRecyclerView.adapter = excerptAdapter
        excerptRecyclerView.layoutManager = LinearLayoutManager(this)

        // 4. 버튼 리스너
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        findViewById<Button>(R.id.openScanButton).setOnClickListener {
            val intent = Intent(this, AddExcerptActivity::class.java).apply {
                putExtra(IntentKeys.BOOK_ID, bookId)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.openExcerptListButton).setOnClickListener {
            val intent = Intent(this, ExcerptListActivity::class.java).apply {
                putExtra(IntentKeys.BOOK_ID, bookId)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.openReviewButton).setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java).apply {
                putExtra(IntentKeys.BOOK_ID, bookId)
            }
            startActivity(intent)
        }

        // 5. DB 데이터 실시간 관찰
        lifecycleScope.launch {
            if (bookId != -1L) {
                // 리뷰 목록 관찰
                launch {
                    AppContainer.reviewRepository.observeReviewsByBook(bookId).collectLatest {
                        reviewAdapter.submitList(it)
                    }
                }

                // 발췌 목록 관찰
                launch {
                    AppContainer.excerptRepository.observeExcerptsByBook(bookId).collectLatest {
                        excerptAdapter.submitList(it)
                    }
                }
            }
        }
    }
}