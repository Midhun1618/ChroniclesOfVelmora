package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import com.voxcom.chroniclesofvelmora.R
import com.voxcom.chroniclesofvelmora.utils.Camera
import kotlin.math.abs

class Enemy(
    context: Context,
    var worldX: Float,
    var worldY: Float
) {

    // -------- FRAMES --------
    private val idleFrame = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.bot_idle),
        105, 240, false
    )

    private val shootFrames = listOf(
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bot_open1), 105, 240, false),
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bot_open2), 105, 240, false),
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bot_open3), 105, 240, false),
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.bot_open4), 105, 240, false)
    )

    private var currentFrame: Bitmap = idleFrame

    private val width = idleFrame.width.toFloat()
    private val height = idleFrame.height.toFloat()

    // -------- PHYSICS --------
    private var velocityX = 200f
    private var velocityY = 0f
    private val gravity = 1400f
    private var isOnGround = false

    // -------- FLIP --------
    private var facingRight = true

    // -------- SHOOTING --------
    private enum class State { IDLE, SHOOTING }
    private var state = State.IDLE

    private var animIndex = 0
    private var animTimer = 0f
    private val animSpeed = 0.07f
    private var reverse = false

    private var shootCooldown = 2f
    private var shootTimer = 0f

    fun update(
        deltaTime: Float,
        platforms: List<Platform>,
        player: Player,
        spawnBullet: (Float, Float, Float) -> Unit
    ) {

        // ---------- GRAVITY ----------
        velocityY += gravity * deltaTime
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

        // ---------- HORIZONTAL ----------
        worldX += velocityX * deltaTime

        for (platform in platforms) {
            if (isColliding(platform)) {
                if (velocityX > 0) {
                    worldX = platform.x - width
                } else {
                    worldX = platform.x + platform.width
                }
                velocityX *= -1f
                facingRight = velocityX > 0
            }
        }

        // ---------- PLAYER DETECTION ----------
        val distanceToPlayer = abs(player.worldX - worldX)
        val playerIsRight = player.worldX > worldX

        val playerInFront =
            (facingRight && playerIsRight) ||
                    (!facingRight && !playerIsRight)

        if (distanceToPlayer < 800f &&
            playerInFront &&
            state == State.IDLE
        ) {
            shootTimer += deltaTime

            if (shootTimer >= shootCooldown) {
                state = State.SHOOTING
                animIndex = 0
                reverse = false
                shootTimer = 0f
            }
        }

        // ---------- SHOOTING ANIMATION ----------
        if (state == State.SHOOTING) {

            animTimer += deltaTime

            if (animTimer >= animSpeed) {
                animTimer = 0f

                if (!reverse) {
                    if (animIndex < shootFrames.size - 1) {
                        animIndex++
                    } else {
                        // SHOOT HERE
                        val direction = if (player.worldX > worldX) 1f else -1f
                        spawnBullet(
                            worldX + width / 2,
                            worldY + height / 2,
                            direction
                        )
                        reverse = true
                    }
                } else {
                    if (animIndex > 0) {
                        animIndex--
                    } else {
                        state = State.IDLE
                    }
                }
            }

            currentFrame = shootFrames[animIndex]

        } else {
            currentFrame = idleFrame
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

        val screenX = worldX - camera.cameraX
        val screenY = worldY - camera.cameraY

        canvas.save()

        if (!facingRight) {
            canvas.scale(1f, 1f, screenX + width / 2, 0f)
        }else{
            canvas.scale(-1f, 1f, screenX + width / 2, 0f)
        }

        canvas.drawBitmap(currentFrame, screenX, screenY, null)

        canvas.restore()
    }
}