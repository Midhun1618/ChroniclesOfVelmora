package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import com.voxcom.chroniclesofvelmora.R
import com.voxcom.chroniclesofvelmora.utils.Camera

class Strike(
    context: Context,
    var worldX: Float,
    var worldY: Float,
    private val direction: Float
) {

    private val speed = 1400f
    var isActive = true

    // ----- LOAD 2 FRAMES -----
    private val frame1 = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.strike_flame1),
        60,
        20,
        false
    )

    private val frame2 = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.strike_flame2),
        120,
        40,
        false
    )

    private var currentFrame: Bitmap = frame1
    private val width = frame1.width.toFloat()
    private val height = frame1.height.toFloat()

    // Animation control
    private var animationTimer = 0f
    private val animationSpeed = 0.2f  // smaller = faster switching

    fun update(deltaTime: Float) {

        // Move
        worldX += direction * speed * deltaTime

        // Animate
        animationTimer += deltaTime

        if (animationTimer >= animationSpeed) {
            currentFrame = if (currentFrame == frame1) frame2 else frame1
            animationTimer = 0f
        }
    }

    fun draw(canvas: Canvas, camera: Camera) {

        val screenX = worldX - camera.cameraX
        val screenY = worldY - camera.cameraY

        val matrix = Matrix()

        if (direction < 0) {
            matrix.preScale(-1f, 1f)
            matrix.postTranslate(screenX + width, screenY)
        } else {
            matrix.postTranslate(screenX, screenY)
        }

        canvas.drawBitmap(currentFrame, matrix, null)
    }

    fun isCollidingWithPlatform(platform: Platform): Boolean {
        return worldX + width > platform.x &&
                worldX < platform.x + platform.width &&
                worldY + height > platform.y &&
                worldY < platform.y + platform.height
    }

    fun isCollidingWithEnemy(enemy: Enemy): Boolean {
        return worldX + width > enemy.worldX &&
                worldX < enemy.worldX + 110f &&
                worldY + height > enemy.worldY &&
                worldY < enemy.worldY + 110f
    }
}