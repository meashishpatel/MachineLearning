package com.example.machinelearning

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.machinelearning.databinding.ActivityRealtimeFaceMeshDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetector
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import java.util.concurrent.Executors

class RealtimeFaceMeshDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRealtimeFaceMeshDetectionBinding
    private lateinit var faceMeshDetector: FaceMeshDetector
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val CAMERA_PERMISSION_CODE = 1001
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private lateinit var overlayView: FaceMeshOverlayView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealtimeFaceMeshDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            setupFaceMeshDetector()
        }

        overlayView = binding.overlayView

        binding.btnSwitchCamera.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            startCamera()
        }
    }

    private fun setupFaceMeshDetector() {
        val options = FaceMeshDetectorOptions.Builder()
            .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
            .build()

        faceMeshDetector = FaceMeshDetection.getClient(options)
        startCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupFaceMeshDetector()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("CameraX", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        faceMeshDetector.process(image)
            .addOnSuccessListener { meshes ->
                for (mesh in meshes) {
                    val faceMeshpoints = mesh.allPoints

                    faceMeshpoints.forEachIndexed { index, faceMeshPoint ->
                        val position = faceMeshPoint.position
                        Log.d("FaceMesh", "Point #$index - x: ${position.x}, y: ${position.y}, z: ${position.z}")
                    }
                    val pointList = faceMeshpoints.map { it.position }
                    val triangleList = mesh.allTriangles
//                    Log.d("MeshPoints000","PointList: $pointList and triagleList $triangleList" )
                    Log.d("ImageWidth000", "${image.width} & ${image.height}")
                    runOnUiThread {
                        overlayView.updateMeshData(
                            pointList,
                            triangleList,
                            610,
                            400,
                            lensFacing == CameraSelector.LENS_FACING_FRONT
                        )
                    }
                }
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("FaceMesh", "Detection failed", e)
                imageProxy.close()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        faceMeshDetector.close()
        cameraExecutor.shutdown()
    }
}