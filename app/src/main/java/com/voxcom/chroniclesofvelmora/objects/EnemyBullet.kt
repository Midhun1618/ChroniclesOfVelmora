package com.voxcom.chroniclesofvelmora.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.voxcom.chroniclesofvelmora.R
import com.voxcom.chroniclesofvelmora.utils.Camera

class EnemyBullet(
    context: Context,
    var worldX: Float,
    var worldY: Float,
    private val direction: Float
) {

    private val speed = 900f
    var isActive = true

    private val bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.red_bullet),
        40,
        20,
        false
    )

    private val width = bitmap.width.toFloat()
    private val height = bitmap.height.toFloat()

    fun update(deltaTime: Float) {
        worldX += direction * speed * deltaTime
    }

    fun draw(canvas: Canvas, camera: Camera) {
        canvas.drawBitmap(
            bitmap,
            worldX - camera.cameraX,
            worldY - camera.cameraY,
            null
        )
    }

    fun isCollidingWithPlayer(player: Player): Boolean {
        return worldX + width > player.worldX &&
                worldX < player.worldX + 120f &&
                worldY + height > player.worldY &&
                worldY < player.worldY + 120f
    }

    fun isCollidingWithPlatform(platform: Platform): Boolean {
        return worldX + width > platform.x &&
                worldX < platform.x + platform.width &&
                worldY + height > platform.y &&
                worldY < platform.y + platform.height
    }
}