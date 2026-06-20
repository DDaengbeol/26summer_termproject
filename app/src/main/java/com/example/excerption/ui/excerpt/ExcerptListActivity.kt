package com.example.excerption.ui.excerpt

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
import com.example.excerption.data.local.entity.ExcerptEntity
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

        findViewById<ImageButton>(R.id.excerptBackButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.addExcerptButton).setOnClickListener {
            lifecycleScope.launch {
                val excerptId = AppContainer.excerptRepository.insertExcerpt(
                    ExcerptEntity(bookId = bookId, content = "")
                )
                startActivity(
                    Intent(this@ExcerptListActivity, ExcerptEditActivity::class.java)
                        .putExtra(IntentKeys.BOOK_ID, bookId)
                        .putExtra(IntentKeys.EXCERPT_ID, excerptId)
                )
            }
        }

        val adapter = ExcerptAdapter(
            onExcerptClick = { excerpt ->
                startActivity(
                    Intent(this, ExcerptEditActivity::class.java)
                        .putExtra(IntentKeys.BOOK_ID, bookId)
                        .putExtra(IntentKeys.EXCERPT_ID, excerpt.id)
                )
            },
            onExcerptDelete = { excerpt -> confirmDeleteExcerpt(excerpt) }
        )

        findViewById<RecyclerView>(R.id.excerptRecyclerView).apply {
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

    private fun confirmDeleteExcerpt(excerpt: ExcerptEntity) {
        AlertDialog.Builder(this)
            .setTitle("발췌 삭제")
            .setMessage("이 발췌를 삭제할까요?")
            .setNegativeButton("취소", null)
            .setPositiveButton("삭제") { _, _ ->
                lifecycleScope.launch {
                    AppContainer.excerptRepository.deleteExcerpt(excerpt)
                }
            }
            .show()
    }
}
