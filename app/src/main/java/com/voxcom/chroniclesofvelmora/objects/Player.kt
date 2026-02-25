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
        200,
        200,
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

        // ---------------- INPUT ----------------

        val controlFactor = if (isOnGround) 1f else airControlFactor

        velocityX += moveX * moveAcceleration * controlFactor * deltaTime

        // Clamp horizontal speed
        velocityX = velocityX.coerceIn(-maxSpeed, maxSpeed)

        // Jetpack / Gravity
        if (moveY < -0.2f) {
            velocityY += jetpackForce * deltaTime
        } else {
            velocityY += gravity * deltaTime
        }

        // ---------------- HORIZONTAL MOVE ----------------

        worldX += velocityX * deltaTime

        for (platform in platforms) {

            if (isColliding(platform)) {

                if (velocityX > 0) {
                    worldX = platform.x - width
                } else if (velocityX < 0) {
                    worldX = platform.x + platform.width
                }

                velocityX = 0f
            }
        }

        // ---------------- VERTICAL MOVE ----------------

        worldY += velocityY * deltaTime
        isOnGround = false

        for (platform in platforms) {

            if (isColliding(platform)) {

                if (velocityY > 0) {
                    worldY = platform.y - height
                    isOnGround = true
                } else if (velocityY < 0) {
                    worldY = platform.y + platform.height
                }

                velocityY = 0f
            }
        }

        // ---------------- FRICTION ----------------

        if (isOnGround && moveX == 0f) {
            velocityX *= 0.8f
        }
    }
    private fun isColliding(platform: Platform): Boolean {

        val playerLeft = worldX
        val playerRight = worldX + width
        val playerTop = worldY
        val playerBottom = worldY + height

        val platLeft = platform.x
        val platRight = platform.x + platform.width
        val platTop = platform.y
        val platBottom = platform.y + platform.height

        return playerRight > platLeft &&
                playerLeft < platRight &&
                playerBottom > platTop &&
                playerTop < platBottom
    }

    fun draw(canvas: Canvas, camera: Camera) {
        val screenX = worldX - camera.cameraX
        val screenY = worldY - camera.cameraY

        canvas.drawBitmap(bitmap, screenX, screenY, null)
    }
}