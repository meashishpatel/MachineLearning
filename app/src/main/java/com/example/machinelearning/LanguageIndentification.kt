package com.example.machinelearning

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.machinelearning.databinding.ActivityLanguageIndentificationBinding
import com.google.mlkit.nl.languageid.LanguageIdentification

class LanguageIndentification : AppCompatActivity() {


    private lateinit var binding: ActivityLanguageIndentificationBinding
    val languageIdentifier = LanguageIdentification.getClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLanguageIndentificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnIdentify.setOnClickListener {
            val identifyLanguage = binding.identifyText.text.toString().trim()

            if (identifyLanguage.isNotEmpty()) {
                languageIdentifier.identifyLanguage(identifyLanguage)
                    .addOnSuccessListener { languageCode ->
                        if (languageCode == "und") {
                            Log.i("IndetifiedLanguage000", "Can't identify language.")
                        } else {
                            Log.i("IndetifiedLanguage000", "Language: $languageCode")
                            binding.identifiedLanguageTV.text = languageCode
                        }
                    }
                    .addOnFailureListener {
                    }

            } else {
                Toast.makeText(this, "Please enter text to Identify", Toast.LENGTH_SHORT).show()
            }
        }
    }
}