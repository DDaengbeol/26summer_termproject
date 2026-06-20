package com.example.excerption.ui.review

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.ReviewEntity
import kotlinx.coroutines.launch

class MemoListActivity : AppCompatActivity() {
    private var bookId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_list)

        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        if (bookId == -1L) {
            Toast.makeText(this, "책 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<ImageButton>(R.id.memoBackButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.addMemoButton).setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java).putExtra(IntentKeys.BOOK_ID, bookId))
        }

        val adapter = ReviewAdapter(
            onReviewClick = { memo ->
                startActivity(
                    Intent(this, ReviewActivity::class.java)
                        .putExtra(IntentKeys.BOOK_ID, bookId)
                        .putExtra(IntentKeys.REVIEW_ID, memo.id)
                )
            },
            onReviewDelete = { memo -> confirmDeleteMemo(memo) }
        )

        findViewById<RecyclerView>(R.id.memoRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MemoListActivity)
            this.adapter = adapter
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppContainer.reviewRepository.observeReviewsByBook(bookId).collect { memos ->
                    adapter.submitList(memos)
                }
            }
        }
    }

    private fun confirmDeleteMemo(memo: ReviewEntity) {
        AlertDialog.Builder(this)
            .setTitle("메모 삭제")
            .setMessage("이 메모를 삭제할까요?")
            .setNegativeButton("취소", null)
            .setPositiveButton("삭제") { _, _ ->
                lifecycleScope.launch {
                    AppContainer.reviewRepository.deleteReview(memo)
                }
            }
            .show()
    }
}
