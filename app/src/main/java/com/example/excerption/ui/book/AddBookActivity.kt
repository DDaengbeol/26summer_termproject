package com.example.excerption.ui.book

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        // 뷰 초기화
        val btnBack = findViewById<ImageButton>(R.id.backButton)
        val etTitle = findViewById<EditText>(R.id.titleEditText)
        val etAuthor = findViewById<EditText>(R.id.authorEditText)
        val btnSave = findViewById<Button>(R.id.saveBookButton)

        // 뒤로가기 버튼 클릭 시 화면 종료
        btnBack.setOnClickListener { finish() }

        // 저장 버튼 클릭 시
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val author = etAuthor.text.toString().trim()

            if (title.isNotEmpty() && author.isNotEmpty()) {
                saveBookToDatabase(title, author)
            } else {
                Toast.makeText(this, "제목과 저자를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBookToDatabase(title: String, author: String) {
        // 코루틴을 사용하여 백그라운드 스레드에서 DB 저장 실행
        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    // ID는 Room의 autoGenerate 옵션에 의해 자동으로 부여되므로 0L 사용
                    val newBook = BookEntity(
                        id = 0L,
                        title = title,
                        author = author,
                        cover = ""
                    )
                    // AppContainer를 통해 Repository 접근
                    AppContainer.bookRepository.insertBook(newBook)
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (success) {
                Toast.makeText(this@AddBookActivity, "책이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddBookActivity, "저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}