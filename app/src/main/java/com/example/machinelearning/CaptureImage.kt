package com.example.machinelearning

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import java.io.File

class CaptureImage(
    private val activity: Activity,
    private val imageView: ImageView,
    private val launcher: ActivityResultLauncher<Intent>
) {
    private var imageUri: Uri? = null

    fun openCamera(): Uri? {
        val photoFile = File(
            activity.getExternalFilesDir("Pictures"),
            "face_photo_${System.currentTimeMillis()}.jpg"
        )
        imageUri = FileProvider.getUriForFile(activity, "${activity.packageName}.provider", photoFile)

        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri)
        launcher.launch(intent)

        return imageUri
    }

    fun handleResult(resultCode: Int, uri: Uri?, onImageReady: (InputImage) -> Unit) {
        if (resultCode == Activity.RESULT_OK && uri != null) {
            imageView.setImageURI(uri)
            try {
                val inputImage = InputImage.fromFilePath(activity, uri)
                onImageReady(inputImage)
            } catch (e: Exception) {
                Toast.makeText(activity, "Image error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}