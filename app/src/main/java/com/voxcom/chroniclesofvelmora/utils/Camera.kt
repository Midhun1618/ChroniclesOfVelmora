package com.voxcom.chroniclesofvelmora.utils

import android.content.res.Resources
import com.voxcom.chroniclesofvelmora.objects.Player

class Camera {

    var cameraX = 0f
    var cameraY = 0f

    fun update(player: Player) {

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        val mapWidth = 3000f
        val mapHeight = 1200f   // Or use background.height.toFloat()

        val targetX = player.worldX - screenWidth / 2
        val targetY = player.worldY - screenHeight / 2

        cameraX += (targetX - cameraX) * 0.1f
        cameraY += (targetY - cameraY) * 0.1f

        // Clamp camera
        cameraX = cameraX.coerceIn(0f, mapWidth - screenWidth)
        cameraY = cameraY.coerceIn(0f, mapHeight - screenHeight)
    }
}