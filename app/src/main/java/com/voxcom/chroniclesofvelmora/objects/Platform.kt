package com.voxcom.chroniclesofvelmora.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.voxcom.chroniclesofvelmora.utils.Camera

class Platform(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {

    private val paint = Paint().apply {
        color = Color.GRAY
    }

    fun draw(canvas: Canvas, camera: Camera) {
        val screenX = x - camera.cameraX
        val screenY = y - camera.cameraY

        canvas.drawRect(
            screenX,
            screenY,
            screenX + width,
            screenY + height,
            paint
        )
    }
}