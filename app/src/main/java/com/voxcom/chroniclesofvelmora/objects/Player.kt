package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.voxcom.chroniclesofvelmora.R
import com.voxcom.chroniclesofvelmora.utils.Camera

class Player(
    context: Context,
    var worldX: Float,
    var worldY: Float
) {

    private val originalBitmap = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.sample_player
    )

    private val bitmap: Bitmap = Bitmap.createScaledBitmap(
        originalBitmap,
        120,
        120,
        false
    )

    private val width = bitmap.width.toFloat()
    private val height = bitmap.height.toFloat()

    // Movement tuning
    private val moveAcceleration = 800f
    private val maxSpeed = 400f
    private val airControlFactor = 0.8f

    private val gravity = 1400f
    private val jetpackForce = -300f

    private var velocityX = 0f
    private var velocityY = 0f

    private var isOnGround = false

    fun update(
        deltaTime: Float,
        platforms: List<Platform>,
        moveX: Float,
        moveY: Float
    ) {

        // -------- HORIZONTAL PHYSICS --------

        val controlFactor = if (isOnGround) 1f else airControlFactor

        velocityX += moveX * moveAcceleration * controlFactor * deltaTime

        // Clamp horizontal speed
        velocityX = velocityX.coerceIn(-maxSpeed, maxSpeed)

        // -------- VERTICAL PHYSICS --------

        if (moveY < -0.2f) {
            velocityY += jetpackForce * deltaTime
        } else {
            velocityY += gravity * deltaTime
        }

        // -------- APPLY MOVEMENT --------

        worldX += velocityX * deltaTime
        worldY += velocityY * deltaTime

        isOnGround = false

        // -------- PLATFORM COLLISION --------

        for (platform in platforms) {

            val playerBottom = worldY + height
            val playerTop = worldY
            val playerRight = worldX + width
            val playerLeft = worldX

            val platformTop = platform.y
            val platformLeft = platform.x
            val platformRight = platform.x + platform.width

            if (
                playerBottom >= platformTop &&
                playerTop < platformTop &&
                playerRight > platformLeft &&
                playerLeft < platformRight &&
                velocityY >= 0
            ) {
                worldY = platformTop - height
                velocityY = 0f
                isOnGround = true
            }
        }

        // -------- FRICTION (Ground only) --------
        if (isOnGround && moveX == 0f) {
            velocityX *= 0.8f
        }

        // -------- WORLD BOUNDS --------
        worldX = worldX.coerceIn(0f, 3000f - width)
    }

    fun draw(canvas: Canvas, camera: Camera) {
        val screenX = worldX - camera.cameraX
        val screenY = worldY - camera.cameraY

        canvas.drawBitmap(bitmap, screenX, screenY, null)
    }
}