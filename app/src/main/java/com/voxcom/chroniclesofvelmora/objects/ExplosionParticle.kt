package com.voxcom.chroniclesofvelmora.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.voxcom.chroniclesofvelmora.utils.Camera
import kotlin.random.Random

class ExplosionParticle(
    var worldX: Float,
    var worldY: Float
) {

    private var velocityX = Random.nextFloat() * 600f - 300f
    private var velocityY = Random.nextFloat() * 600f - 300f

    private val gravity = 800f
    private var life = 1f
    private val decaySpeed = 1.5f

    var isDead = false

    private val paint = Paint().apply {
        color = Color.GREEN
    }

    fun update(deltaTime: Float) {

        velocityY += gravity * deltaTime

        worldX += velocityX * deltaTime
        worldY += velocityY * deltaTime

        life -= decaySpeed * deltaTime

        if (life <= 0f) {
            life = 0f
            isDead = true
        }

        paint.alpha = (life * 255).toInt()
    }

    fun draw(canvas: Canvas, camera: Camera) {
        canvas.drawCircle(
            worldX - camera.cameraX,
            worldY - camera.cameraY,
            6f,
            paint
        )
    }
}