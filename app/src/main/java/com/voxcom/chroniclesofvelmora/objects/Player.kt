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
        R.drawable.baseplayer
    )

    private val bodyBitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.basebody),
        140,
        220,
        false
    )

    private val armBitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.basehandgun),
        140,
        220,
        false
    )

    // -------- JETPACK FRAMES --------

    private val idleJetpack = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.basejet),
        140,
        220,
        false
    )

    private val rocket1 = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.rocket_fire1),
        140,
        220,
        false
    )

    private val rocket2 = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.rocket_fire2),
        140,
        220,
        false
    )

    private val rocket3 = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.rocket_fire3),
        140,
        220,
        false
    )

    private val width = bodyBitmap.width.toFloat()
    private val height = bodyBitmap.height.toFloat()


    // Movement tuning
    private val moveAcceleration = 800f
    private val maxSpeed = 400f
    private val airControlFactor = 0.8f

    private val gravity = 1400f
    private val jetpackForce = -300f

    private var velocityX = 0f
    private var velocityY = 0f

    private var isOnGround = false
    private var facingDirection = 1f

    private var jetpackActive = false

    private var animationTimer = 0f
    private val animationSpeed = 0.08f
    private var animationIndex = 0

    private val flyingFrames = listOf(rocket1, rocket2, rocket3)
    fun update(
        deltaTime: Float,
        platforms: List<Platform>,
        moveX: Float,
        moveY: Float
    ) {

        val controlFactor = if (isOnGround) 1f else airControlFactor

        velocityX += moveX * moveAcceleration * controlFactor * deltaTime

        velocityX = velocityX.coerceIn(-maxSpeed, maxSpeed)

        if (moveY < -0.2f) {
            velocityY += jetpackForce * deltaTime
            jetpackActive = true
        } else {
            velocityY += gravity * deltaTime
            jetpackActive = false
        }
        if (jetpackActive) {
            animationTimer += deltaTime

            if (animationTimer >= animationSpeed) {
                animationIndex++
                if (animationIndex >= flyingFrames.size) {
                    animationIndex = 0
                }
                animationTimer = 0f
            }
        } else {
            animationIndex = 0
            animationTimer = 0f
        }

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

        if (isOnGround && moveX == 0f) {
            velocityX *= 0.8f
        }
        if (moveX > 0) facingDirection = 1f
        if (moveX < 0) facingDirection = -1f
    }
    var maxHealth = 100
    var currentHealth = 100

    fun takeDamage(amount: Int) {
        currentHealth -= amount
        if (currentHealth < 0) currentHealth = 0
    }

    fun isDead(): Boolean {
        return currentHealth <= 0
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

        // 1️⃣ Jetpack (back layer)
        // -------- JETPACK --------
        val jetpackBitmap = if (jetpackActive) {
            flyingFrames[animationIndex]
        } else {
            idleJetpack
        }

        canvas.drawBitmap(jetpackBitmap, screenX, screenY, null)

        // 2️⃣ Body (middle)
        canvas.drawBitmap(bodyBitmap, screenX, screenY, null)

        // 3️⃣ Arm + Gun (front)
        canvas.drawBitmap(armBitmap, screenX, screenY, null)
    }
    fun getFacingDirection(): Float {
        return if (velocityX >= 0f) 1f else -1f
    }
}