package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.voxcom.chroniclesofvelmora.R
import com.voxcom.chroniclesofvelmora.utils.Camera

class Enemy(
    context: Context,
    var worldX: Float,
    var worldY: Float
) {

    private val bitmap: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.sample_enemy),
        200,
        200,
        false
    )

    private val width = bitmap.width.toFloat()
    private val height = bitmap.height.toFloat()

    private var velocityX = 200f
    private var velocityY = 0f

    private val gravity = 1400f
    private var isOnGround = false

    fun update(deltaTime: Float, platforms: List<Platform>) {

        // Gravity
        velocityY += gravity * deltaTime

        // Horizontal move
        worldX += velocityX * deltaTime

        // Horizontal collision
        for (platform in platforms) {
            if (isColliding(platform)) {
                if (velocityX > 0) {
                    worldX = platform.x - width
                } else {
                    worldX = platform.x + platform.width
                }
                velocityX *= -1f   // Turn around
            }
        }

        // Vertical move
        worldY += velocityY * deltaTime
        isOnGround = false

        for (platform in platforms) {
            if (isColliding(platform)) {
                if (velocityY > 0) {
                    worldY = platform.y - height
                    isOnGround = true
                } else {
                    worldY = platform.y + platform.height
                }
                velocityY = 0f
            }
        }
    }

    private fun isColliding(platform: Platform): Boolean {

        val left = worldX
        val right = worldX + width
        val top = worldY
        val bottom = worldY + height

        val pLeft = platform.x
        val pRight = platform.x + platform.width
        val pTop = platform.y
        val pBottom = platform.y + platform.height

        return right > pLeft &&
                left < pRight &&
                bottom > pTop &&
                top < pBottom
    }

    fun draw(canvas: Canvas, camera: Camera) {
        canvas.drawBitmap(bitmap, worldX - camera.cameraX, worldY - camera.cameraY, null)
    }
}