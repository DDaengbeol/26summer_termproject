package com.example.excerption.ui.excerpt

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.ExcerptEntity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExcerptEditActivity : AppCompatActivity() {
    private lateinit var pageEditText: EditText
    private lateinit var contentEditText: EditText
    private var bookId: Long = -1
    private var excerptId: Long = -1
    private var currentExcerpt: ExcerptEntity? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { recognizeText(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excerpt_edit)

        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        excerptId = intent.getLongExtra(IntentKeys.EXCERPT_ID, -1)
        if (bookId == -1L || excerptId == -1L) {
            Toast.makeText(this, "발췌 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        pageEditText = findViewById(R.id.editExcerptPageEditText)
        contentEditText = findViewById(R.id.editExcerptContentEditText)

        findViewById<ImageButton>(R.id.excerptEditBackButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.saveExcerptEditButton).setOnClickListener { saveExcerpt() }
        findViewById<ImageButton>(R.id.deleteExcerptEditButton).setOnClickListener { confirmDeleteExcerpt() }
        findViewById<Button>(R.id.extractTextButton).setOnClickListener {
            imagePicker.launch("image/*")
        }
        findViewById<Button>(R.id.saveExcerptImageButton).setOnClickListener {
            saveExcerptAsImage()
        }

        loadExcerpt()
    }

    private fun loadExcerpt() {
        lifecycleScope.launch {
            currentExcerpt = AppContainer.excerptRepository.getExcerpt(excerptId)
            currentExcerpt?.let { excerpt ->
                pageEditText.setText(excerpt.page?.toString().orEmpty())
                contentEditText.setText(excerpt.content)
            }
        }
    }

    private fun recognizeText(imageUri: Uri) {
        val image = runCatching { InputImage.fromFilePath(this, imageUri) }
            .onFailure {
                Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            .getOrNull() ?: return

        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        recognizer.process(image)
            .addOnSuccessListener { result ->
                contentEditText.setText(result.text)
                contentEditText.setSelection(contentEditText.text.length)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "텍스트 추출 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveExcerpt() {
        val excerpt = currentExcerpt ?: return
        val content = contentEditText.text.toString().trim()
        if (content.isBlank()) {
            Toast.makeText(this, "발췌 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            AppContainer.excerptRepository.updateExcerpt(
                excerpt.copy(
                    content = content,
                    page = pageEditText.text.toString().toIntOrNull()
                )
            )
            Toast.makeText(this@ExcerptEditActivity, "발췌를 저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun confirmDeleteExcerpt() {
        val excerpt = currentExcerpt ?: return
        AlertDialog.Builder(this)
            .setTitle("발췌 삭제")
            .setMessage("이 발췌를 삭제할까요?")
            .setNegativeButton("취소", null)
            .setPositiveButton("삭제") { _, _ ->
                lifecycleScope.launch {
                    AppContainer.excerptRepository.deleteExcerpt(excerpt)
                    finish()
                }
            }
            .show()
    }

    private fun saveExcerptAsImage() {
        val content = contentEditText.text.toString().trim()
        if (content.isBlank()) {
            Toast.makeText(this, "이미지로 저장할 발췌를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val saved = withContext(Dispatchers.IO) {
                runCatching {
                    val bitmap = createExcerptBitmap(content, pageEditText.text.toString().trim())
                    saveBitmapToGallery(bitmap)
                }.isSuccess
            }
            Toast.makeText(
                this@ExcerptEditActivity,
                if (saved) "발췌 이미지를 저장했습니다." else "이미지 저장에 실패했습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createExcerptBitmap(content: String, pageText: String): Bitmap {
        val width = 1080
        val height = 1440
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 36, 33)
            textSize = 48f
        }
        val quotePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 36, 33)
            textSize = 56f
            isFakeBoldText = true
        }
        val metaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(96, 102, 108)
            textSize = 34f
        }

        canvas.drawText("“", 96f, 150f, quotePaint)
        val layout = StaticLayout.Builder
            .obtain(content, 0, content.length, textPaint, width - 192)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(10f, 1f)
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(96f, 220f)
        layout.draw(canvas)
        canvas.restore()

        canvas.drawText("”", width - 132f, height - 190f, quotePaint)
        if (pageText.isNotBlank()) {
            canvas.drawText("${pageText}쪽", 96f, height - 112f, metaPaint)
        }
        canvas.drawText("Excerption", width - 280f, height - 112f, metaPaint)
        return bitmap
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val fileName = "excerption-${System.currentTimeMillis()}.png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Excerption")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Failed to create image")
        resolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        } ?: error("Failed to open image")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    }
}
