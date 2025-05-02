package com.example.machinelearning

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.facemesh.FaceMeshPoint
import com.google.mlkit.vision.common.Triangle

class FaceMeshOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val points = mutableListOf<PointF3D>()
    private val triangles = mutableListOf<Triangle<FaceMeshPoint>>()

    private val pointPaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 6f
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.YELLOW
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private var imageWidth = 1
    private var imageHeight = 1
    private var isFrontCamera = true

    fun updateMeshData(
        newPoints: List<PointF3D>,
        newTriangles: List<Triangle<FaceMeshPoint>>,
        imgWidth: Int,
        imgHeight: Int,
        isFront: Boolean
    ) {
        points.clear()
        triangles.clear()

        imageWidth = imgWidth
        imageHeight = imgHeight
        isFrontCamera = isFront

        points.addAll(newPoints)
        triangles.addAll(newTriangles)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val scaleX = width.toFloat() / imageHeight
        val scaleY = height.toFloat() / imageWidth

        if (isFrontCamera) {
            canvas.scale(-1f, 1f, (width / 2).toFloat(), (height / 2).toFloat())
        }

        for (triangle in triangles) {
            val pts = triangle.allPoints
            if (pts.size == 3) {
                val p1 = pts[0].position
                val p2 = pts[1].position
                val p3 = pts[2].position

                canvas.drawLine(p1.x * scaleX, p1.y * scaleY, p2.x * scaleX, p2.y * scaleY, linePaint)
                canvas.drawLine(p2.x * scaleX, p2.y * scaleY, p3.x * scaleX, p3.y * scaleY, linePaint)
                canvas.drawLine(p3.x * scaleX, p3.y * scaleY, p1.x * scaleX, p1.y * scaleY, linePaint)
            }
        }

        for (point in points) {
            canvas.drawCircle(point.x * scaleX, point.y * scaleY, 4f, pointPaint)
        }
    }
}