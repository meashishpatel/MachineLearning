package com.example.machinelearning

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.machinelearning.databinding.ActivityEntityExtractionBinding
import com.google.mlkit.nl.entityextraction.DateTimeEntity
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.google.mlkit.nl.entityextraction.FlightNumberEntity
import com.google.mlkit.nl.entityextraction.MoneyEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EntityExtractionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntityExtractionBinding
    private lateinit var entityExtractor: EntityExtractor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEntityExtractionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH)
                    .build())
        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener {
                Log.d("Model000", "Model Downloaded")
            }
            .addOnFailureListener{
                Log.d("Model000", "Model Downloaded Failed")
            }
            binding.sendBtn.setOnClickListener{
                val text = binding.editText.text.toString()
                if(text.isNotEmpty()){
                    executeEntityExtraction(text)
                    binding.editText.text.clear()
                } else{
                    Toast.makeText(this, "Please Enter Some message first", Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun executeEntityExtraction(text: String) {
        val params = EntityExtractionParams.Builder(text).build()
        val stringBuilder = StringBuilder()

        entityExtractor
            .annotate(params)
            .addOnSuccessListener { entityAnnotations ->
                val size = entityAnnotations.size
                Log.d("result000", "Total Entities: $size")
                stringBuilder.append("Total Entities: $size\n\n")

                for (annotation in entityAnnotations) {
                    val entities: List<Entity> = annotation.entities

                    for (entity in entities) {
                        when (entity) {
                            is FlightNumberEntity -> {
                                val airline = entity.airlineCode
                                val flightNumber = entity.flightNumber
                                stringBuilder.append("Flight: $airline $flightNumber\n")
                                Log.d("Values000", "Airline Code: $airline")
                                Log.d("Values000", "Flight number: $flightNumber")
                            }

                            is DateTimeEntity -> {
                                val timestamp = entity.timestampMillis
                                val formattedDate = Instant.ofEpochMilli(timestamp)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                stringBuilder.append("Date/Time: $formattedDate\n")
                                Log.d("Values000", "TimeStamp: $formattedDate")
                            }
                            is MoneyEntity -> {
                                val money = entity.integerPart
                                stringBuilder.append("Money: $money\n")
                            }

                            else -> {
                                val rawValue = entity
                                stringBuilder.append("Other Entity: $rawValue\n")
                                Log.d("Values000", "Other Entity: $rawValue")
                            }
                        }
                    }
                }
                binding.extractedEntity.text = stringBuilder.toString()
            }
            .addOnFailureListener {
                Log.e("EntityExtraction", "Failed to extract entities", it)
                binding.extractedEntity.text = "Entity extraction failed. Try again."
            }
    }
}