package com.example.excerption.ui.scan

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.excerption.R
import com.example.excerption.common.IntentKeys
import com.example.excerption.data.AppContainer
import com.example.excerption.data.local.entity.ExcerptEntity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.File
import kotlinx.coroutines.launch

class TextScanActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var recognizedTextEditText: EditText
    private lateinit var pageEditText: EditText
    private var imageCapture: ImageCapture? = null
    private var bookId: Long = -1

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_scan)

        bookId = intent.getLongExtra(IntentKeys.BOOK_ID, -1)
        if (bookId == -1L) {
            Toast.makeText(this, "책 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        previewView = findViewById(R.id.cameraPreviewView)
        recognizedTextEditText = findViewById(R.id.recognizedTextEditText)
        pageEditText = findViewById(R.id.pageEditText)

        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.captureTextButton).setOnClickListener {
            captureAndRecognize()
        }

        findViewById<View>(R.id.galleryImageButton).setOnClickListener {
            Toast.makeText(this, "사진 선택 기능은 갤러리 연동 시 연결하면 됩니다.", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.deleteScanImageButton).setOnClickListener {
            recognizedTextEditText.text?.clear()
        }

        findViewById<View>(R.id.saveExcerptButton).setOnClickListener {
            saveExcerpt()
        }

        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder().build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun captureAndRecognize() {
        val capture = imageCapture ?: return
        val imageDir = File(cacheDir, "scan_images").apply { mkdirs() }
        val imageFile = File(imageDir, "scan-${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    recognizeText(Uri.fromFile(imageFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@TextScanActivity,
                        "촬영에 실패했습니다: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun recognizeText(imageUri: Uri) {
        val image = InputImage.fromFilePath(this, imageUri)
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        recognizer.process(image)
            .addOnSuccessListener { result ->
                recognizedTextEditText.setText(result.text)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "텍스트 추출 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveExcerpt() {
        val content = recognizedTextEditText.text.toString().trim()
        if (content.isBlank()) {
            Toast.makeText(this, "저장할 문장을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            AppContainer.excerptRepository.insertExcerpt(
                ExcerptEntity(
                    bookId = bookId,
                    content = content,
                    page = pageEditText.text.toString().toIntOrNull()
                )
            )
            Toast.makeText(this@TextScanActivity, "발췌를 저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
