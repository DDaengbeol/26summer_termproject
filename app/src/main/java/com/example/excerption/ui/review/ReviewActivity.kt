package com.example.excerption.ui.review

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.ReviewEntity
import kotlinx.coroutines.launch

class ReviewActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewEditText: EditText
    private var bookId: Long = -1
    private var reviewId: Long = -1
    private var currentReview: ReviewEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // 1. Intent로 전달된 데이터 확인
        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        reviewId = intent.getLongExtra(IntentKeys.REVIEW_ID, -1)

        Log.d("REVIEW_DEBUG", "진입 - bookId: $bookId, reviewId: $reviewId")

        if (bookId == -1L) {
            Toast.makeText(this, "책 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ratingBar = findViewById(R.id.ratingBar)
        reviewEditText = findViewById(R.id.reviewEditText)

        findViewById<View>(R.id.backButton).setOnClickListener { finish() }

        // 2. 기존 리뷰 로드 시도
        if (reviewId != -1L) {
            loadReview()
        }

        findViewById<Button>(R.id.saveReviewButton).setOnClickListener {
            saveReview()
        }
    }

    private fun loadReview() {
        lifecycleScope.launch {
            Log.d("REVIEW_DEBUG", "DB 데이터 로드 시도... ID: $reviewId")

            // AppContainer를 통해 저장소에 접근
            val review = AppContainer.reviewRepository.getReview(reviewId)

            if (review != null) {
                currentReview = review
                ratingBar.rating = review.rating
                reviewEditText.setText(review.content)
                Log.d("REVIEW_DEBUG", "데이터 로드 성공: ${review.content}")
            } else {
                Log.e("REVIEW_DEBUG", "데이터 로드 실패: 해당 ID의 리뷰가 DB에 없음!")
                Toast.makeText(this@ReviewActivity, "리뷰를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveReview() {
        val content = reviewEditText.text.toString().trim()
        if (content.isBlank()) {
            Toast.makeText(this, "리뷰 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingReview = currentReview
            if (existingReview == null) {
                // 신규 저장
                val newReview = ReviewEntity(
                    bookId = bookId,
                    rating = ratingBar.rating,
                    content = content
                )
                AppContainer.reviewRepository.insertReview(newReview)
                Log.d("REVIEW_DEBUG", "신규 리뷰 저장 완료")
            } else {
                // 수정
                AppContainer.reviewRepository.updateReview(
                    existingReview.copy(
                        rating = ratingBar.rating,
                        content = content,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                Log.d("REVIEW_DEBUG", "리뷰 업데이트 완료")
            }

            Toast.makeText(this@ReviewActivity, "리뷰를 저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}