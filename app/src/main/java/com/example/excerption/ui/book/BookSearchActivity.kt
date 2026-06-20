package com.example.excerption.ui.book

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.excerption.BuildConfig
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.remote.AladinBookApi
import com.example.excerption.data.remote.OnlineBook
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookSearchActivity : AppCompatActivity() {
    private lateinit var adapter: BookSearchAdapter
    private lateinit var queryEditText: EditText
    private lateinit var emptyTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        adapter = BookSearchAdapter { openBookEdit(it) }
        queryEditText = findViewById(R.id.bookSearchEditText)
        queryEditText.inputType =
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
        queryEditText.imeOptions = EditorInfo.IME_ACTION_SEARCH
        queryEditText.setSingleLine(true)
        emptyTextView = findViewById(R.id.bookSearchEmptyTextView)
        progressBar = findViewById(R.id.bookSearchProgressBar)

        findViewById<ImageButton>(R.id.bookSearchBackButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.bookSearchSubmitButton).setOnClickListener {
            searchJob?.cancel()
            search(showBlankMessage = true)
        }

        findViewById<RecyclerView>(R.id.bookSearchRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@BookSearchActivity)
            adapter = this@BookSearchActivity.adapter
        }

        queryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchJob?.cancel()
                search(showBlankMessage = true)
                true
            } else {
                false
            }
        }

        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                if (s?.toString()?.trim().isNullOrBlank()) {
                    adapter.submitList(emptyList())
                    progressBar.isVisible = false
                    emptyTextView.isVisible = true
                    emptyTextView.text = "알라딘에서 책을 검색해 등록하세요."
                    return
                }

                searchJob = lifecycleScope.launch {
                    delay(450)
                    search(showBlankMessage = false)
                }
            }
        })

        queryEditText.requestFocus()
        queryEditText.post {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(queryEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroy() {
        searchJob?.cancel()
        super.onDestroy()
    }

    private fun search(showBlankMessage: Boolean) {
        val query = queryEditText.text.toString().trim()
        if (query.isBlank()) {
            if (showBlankMessage) {
                Toast.makeText(this, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        lifecycleScope.launch {
            progressBar.isVisible = true
            emptyTextView.isVisible = false
            runCatching {
                AladinBookApi(aladinTtbKey()).search(query)
            }.onSuccess { books ->
                adapter.submitList(books)
                emptyTextView.isVisible = books.isEmpty()
                emptyTextView.text = "검색 결과가 없습니다."
            }.onFailure { error ->
                adapter.submitList(emptyList())
                emptyTextView.isVisible = true
                emptyTextView.text = error.message ?: "책 검색에 실패했습니다."
            }
            progressBar.isVisible = false
        }
    }

    private fun aladinTtbKey(): String {
        return BuildConfig.ALADIN_TTB_KEY.ifBlank {
            getString(R.string.aladin_ttb_key)
        }
    }

    private fun openBookEdit(book: OnlineBook) {
        startActivity(
            Intent(this, AddBookActivity::class.java)
                .putExtra(IntentKeys.BOOK_TITLE, book.title)
                .putExtra(IntentKeys.BOOK_AUTHOR, book.author)
                .putExtra(IntentKeys.BOOK_PUBLISHER, book.publisher)
                .putExtra(IntentKeys.BOOK_ISBN, book.isbn)
                .putExtra(IntentKeys.BOOK_CATEGORY, book.categoryName)
                .putExtra(IntentKeys.BOOK_COVER_URL, book.coverUrl)
                .putExtra(IntentKeys.BOOK_LINK, book.link)
        )
    }
}
