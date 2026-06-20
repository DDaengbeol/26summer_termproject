package com.example.excerption.ui.review

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.ReviewEntity
import kotlinx.coroutines.launch

class ReviewActivity : AppCompatActivity() {
    private lateinit var memoEditText: EditText
    private var bookId: Long = -1
    private var reviewId: Long = -1
    private var currentMemo: ReviewEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        reviewId = intent.getLongExtra(IntentKeys.REVIEW_ID, -1)
        if (bookId == -1L) {
            Toast.makeText(this, "책 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        memoEditText = findViewById(R.id.reviewEditText)
        findViewById<ImageButton>(R.id.reviewBackButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.deleteReviewButton).apply {
            isVisible = reviewId != -1L
            setOnClickListener { deleteMemo() }
        }
        if (reviewId != -1L) {
            loadMemo()
        }

        findViewById<ImageButton>(R.id.saveReviewButton).setOnClickListener {
            saveMemo()
        }
    }

    private fun loadMemo() {
        lifecycleScope.launch {
            currentMemo = AppContainer.reviewRepository.getReview(reviewId)
            currentMemo?.let { memo ->
                memoEditText.setText(memo.content)
            }
        }
    }

    private fun saveMemo() {
        val content = memoEditText.text.toString().trim()
        if (content.isBlank()) {
            Toast.makeText(this, "메모 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingMemo = currentMemo
            if (existingMemo == null) {
                AppContainer.reviewRepository.insertReview(
                    ReviewEntity(
                        bookId = bookId,
                        rating = 0f,
                        content = content
                    )
                )
            } else {
                AppContainer.reviewRepository.updateReview(
                    existingMemo.copy(
                        content = content,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            Toast.makeText(this@ReviewActivity, "메모를 저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun deleteMemo() {
        val memo = currentMemo ?: return finish()
        lifecycleScope.launch {
            AppContainer.reviewRepository.deleteReview(memo)
            finish()
        }
    }
}
