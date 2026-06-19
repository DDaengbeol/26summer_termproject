package com.example.excerption.ui.excerpt

import android.os.Bundle
import android.view.View
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
import kotlinx.coroutines.launch

class ExcerptListActivity : AppCompatActivity() {
    private var bookId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excerpt_list)

        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        if (bookId == -1L) {
            Toast.makeText(this, "책 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val backButton = findViewById<View>(R.id.backButton)
        val recyclerView = findViewById<RecyclerView>(R.id.excerptRecyclerView)
        backButton.setOnClickListener { finish() }

        // [수정 완료] 생성자 2개(클릭/삭제)를 모두 전달하도록 변경
        val adapter = ExcerptAdapter(
            onExcerptClick = { excerpt ->
                Toast.makeText(this, "발췌 아이템 클릭됨", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { excerpt ->
                // 삭제 로직 추가
                lifecycleScope.launch {
                    AppContainer.excerptRepository.deleteExcerpt(excerpt)
                }
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ExcerptListActivity)
            this.adapter = adapter
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppContainer.excerptRepository.observeExcerptsByBook(bookId).collect { excerpts ->
                    adapter.submitList(excerpts)
                }
            }
        }
    }
}