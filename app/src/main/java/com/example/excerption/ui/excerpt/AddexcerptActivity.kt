package com.example.excerption.ui.excerpt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.ExcerptEntity
import kotlinx.coroutines.launch

class AddExcerptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_excerpt)

        val bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        val contentInput = findViewById<EditText>(R.id.excerptContentInput)
        val pageInput = findViewById<EditText>(R.id.excerptPageInput) // 페이지 입력창
        val saveButton = findViewById<Button>(R.id.saveExcerptButton)

        saveButton.setOnClickListener {
            val content = contentInput.text.toString()
            val pageString = pageInput.text.toString()
            val page = pageString.toIntOrNull() ?: 0 // 값이 없으면 0, 있으면 숫자로 변환

            if (content.isNotBlank()) {
                val newExcerpt = ExcerptEntity(
                    bookId = bookId,
                    content = content,
                    page = page
                )
                lifecycleScope.launch {
                    AppContainer.excerptRepository.insertExcerpt(newExcerpt)
                    finish()
                }
            }
        }
    }
}