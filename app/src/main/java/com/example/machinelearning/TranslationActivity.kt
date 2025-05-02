package com.example.machinelearning

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.machinelearning.databinding.ActivityMainBinding
import com.example.machinelearning.databinding.ActivityTranslationBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI)
            .build()
        val englishHindiTranslator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .build()

        englishHindiTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Toast.makeText(this, "Translation model ready", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Model download failed: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        binding.btnTranslate.setOnClickListener {
            val textToTranslate = binding.translationText.text.toString().trim()

            if (textToTranslate.isNotEmpty()) {
                englishHindiTranslator.translate(textToTranslate)
                    .addOnSuccessListener { translatedText ->
                        binding.translatedTextView.text = translatedText
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Translation failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            }
        }
    }
}