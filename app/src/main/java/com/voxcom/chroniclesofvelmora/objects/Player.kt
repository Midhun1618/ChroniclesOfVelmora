package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.voxcom.chroniclesofvelmora.R
import com.voxcom.chroniclesofvelmora.utils.Camera

class Player(
    context: Context,
    var worldX: Float,
    var worldY: Float
) {

    private val bitmap = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.sample_player
    )

    private val width = bitmap.width.toFloat()
    private val height = bitmap.height.toFloat()

    var speed = 12f
    var moveLeft = false
    var moveRight = false

    // PHYSICS
    private var velocityY = 0f
    private val gravity = 1.2f
    private val jumpPower = -25f
    private val groundY = 900f
    private var isOnGround = false

    fun update() {

        // Horizontal
        if (moveLeft) worldX -= speed
        if (moveRight) worldX += speed

        // Apply gravity
        velocityY += gravity
        worldY += velocityY

        // Ground collision
        if (worldY + height >= groundY) {
            worldY = groundY - height
            velocityY = 0f
            isOnGround = true
        } else {
            isOnGround = false
        }

        // World boundary
        worldX = worldX.coerceIn(0f, 3000f - width)
    }

    fun jump() {
        if (isOnGround) {
            velocityY = jumpPower
            isOnGround = false
        }
    }

    fun draw(canvas: Canvas, camera: Camera) {
        val screenX = worldX - camera.cameraX
        val screenY = worldY - camera.cameraY

        canvas.drawBitmap(bitmap, screenX, screenY, null)
    }
}