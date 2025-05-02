package com.example.machinelearning

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.machinelearning.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.faceDetection.setOnClickListener {
            startActivity(Intent(this, FaceDetectionActivity::class.java))
        }
        binding.realTimefaceDetection.setOnClickListener {
            startActivity(Intent(this, RealTimeFaceDetection::class.java))
        }
        binding.RealtimetextRecognition.setOnClickListener {
            startActivity(Intent(this, TextRecognitionActivity::class.java))
        }
        binding.textRecognition.setOnClickListener {
            startActivity(Intent(this, StaticTextRecognitionActivity::class.java))
        }
        binding.ObjectRecognition.setOnClickListener {
            startActivity(Intent(this, ImageLabellingActivity::class.java))
        }
        binding.smartReplies.setOnClickListener {
            startActivity(Intent(this, SmartReplyActivity::class.java))
        }
        binding.faceMeshDetection.setOnClickListener {
            startActivity(Intent(this, RealtimeFaceMeshDetectionActivity::class.java))
        }
        binding.documentScanner.setOnClickListener {
            startActivity(Intent(this, DocumentScanner::class.java))
        }
        binding.entityExtraction.setOnClickListener {
            startActivity(Intent(this, EntityExtractionActivity::class.java))
        }
        binding.translation.setOnClickListener {
            startActivity(Intent(this, TranslationActivity::class.java))
        }
        binding.languageIdentification.setOnClickListener {
            startActivity(Intent(this, LanguageIndentification::class.java))
        }
    }
}