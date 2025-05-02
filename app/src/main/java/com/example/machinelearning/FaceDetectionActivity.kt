package com.example.machinelearning

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setMargins
import com.example.machinelearning.databinding.ActivityFaceDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceDetectionBinding
    private var imageUri: Uri? = null
    private lateinit var captureImage: CaptureImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
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

        binding.startFaceDetectionBtn.setOnClickListener {
            captureImage = CaptureImage(this, binding.imageView, captureImageLauncher)
            imageUri = captureImage.openCamera()
        }
    }

    // Function to pick image from gallery
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    try {
                        binding.imageView.setImageURI(it)
                        val inputImage = InputImage.fromFilePath(this, it)
                        processImage(inputImage)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun processImage(image: InputImage) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(highAccuracyOpts)
        Log.d("abcdef0000", "detector= $detector")

        // Clear previous results
        (binding.main as ViewGroup).removeViews(2, (binding.main as ViewGroup).childCount - 2)

        detector.process(image)
            .addOnSuccessListener { faces ->
                Log.d("abcdef0000", "faces= $faces")
                for ((index, face) in faces.withIndex()) {
                    val bounds = face.boundingBox
                    val rotY = face.headEulerAngleY
                    val rotZ = face.headEulerAngleZ
                    val smileProb = face.smilingProbability
                    val rightEyeOpenProb = face.rightEyeOpenProbability
                    val id = face.trackingId
                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)?.position
                    val upperLipBottomContour =
                        face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                    val infoText = StringBuilder()
                    infoText.append("Face $index:\n")
                    infoText.append("Smile Prob: ${"%.2f".format(smileProb)}\n")
                    infoText.append("Right Eye Open Prob: ${"%.2f".format(rightEyeOpenProb)}\n")
                    infoText.append("Tracking ID: $id\n")
                    infoText.append("Bounds: $bounds\n")
                    infoText.append("Rotation Y: ${"%.2f".format(rotY)}°\n")
                    infoText.append("Rotation Z: ${"%.2f".format(rotZ)}°\n")
                    if (leftEar != null) {
                        infoText.append("Left Ear Pos: ${leftEar.x}, ${leftEar.y}\n")
                    }
                    if (upperLipBottomContour != null) {
                        infoText.append("Upper Lip Points: ${upperLipBottomContour.size}\n")
                    }

                    val textView = TextView(this).apply {
                        text = infoText.toString()
                        setTextColor(resources.getColor(android.R.color.black, null))
                        setBackgroundColor(0xAAFFFFFF.toInt())
                        textSize = 12f
                        setPadding(12, 8, 12, 8)
                    }

                    val params = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(bounds.left)
                    textView.layoutParams = params

                    (binding.main as ViewGroup).addView(textView)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Detection failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}