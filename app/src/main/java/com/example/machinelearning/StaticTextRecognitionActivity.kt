package com.example.machinelearning

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setMargins
import com.example.machinelearning.databinding.ActivityFaceDetectionBinding
import com.example.machinelearning.databinding.ActivityStaticTextRecognitionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class StaticTextRecognitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaticTextRecognitionBinding
    private var imageUri: Uri? = null
    private lateinit var textRecognition: TextRecognizer
    private lateinit var captureImage: CaptureImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStaticTextRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val captureImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                captureImage.handleResult(result.resultCode, imageUri) { inputImage ->
                    processImage(inputImage)
                }
            }

        binding.startTextDetectionBtn.setOnClickListener {
            captureImage = CaptureImage(this, binding.imageView1, captureImageLauncher)
            imageUri = captureImage.openCamera()
        }
    }

    private fun processImage(image: InputImage) {

        textRecognition = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        // Clear previous results
        (binding.main as ViewGroup).removeViews(2, (binding.main as ViewGroup).childCount - 2)

        textRecognition.process(image)
            .addOnSuccessListener { visionTexts ->
                var result = ""
                for (block in visionTexts.textBlocks) {
                    for (line in block.lines) {
                        for (element in line.elements) {
                            val elementText = element.text
                            result += " $elementText"
                        }
                    }
                }
                runOnUiThread {
                    binding.textResult.text = result.trim()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Detection error", e)
            }
    }
}