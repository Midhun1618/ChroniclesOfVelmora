package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.voxcom.chroniclesofvelmora.utils.Camera

class Platform(
    context: Context,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    imageResId: Int
) {

    private val tileBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, imageResId)

    private val tileWidth = tileBitmap.width
    private val tileHeight = tileBitmap.height

    fun draw(canvas: Canvas, camera: Camera) {

        val startX = x - camera.cameraX
        val startY = y - camera.cameraY

        var drawX = 0f
        while (drawX < width) {

            var drawY = 0f
            while (drawY < height) {

                canvas.drawBitmap(
                    tileBitmap,
                    startX + drawX,
                    startY + drawY,
                    null
                )

                drawY += tileHeight
            }

            drawX += tileWidth
        }
    }
}