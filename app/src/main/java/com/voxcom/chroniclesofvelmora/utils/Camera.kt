package com.voxcom.chroniclesofvelmora.utils

import android.content.res.Resources
import com.voxcom.chroniclesofvelmora.objects.Player

class Camera {

    var cameraX = 0f
    var cameraY = 0f

    fun update(player: Player) {

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        val targetX = player.worldX - screenWidth / 2
        val targetY = player.worldY - screenHeight / 2

        // smooth follow
        cameraX += (targetX - cameraX) * 0.1f
        cameraY += (targetY - cameraY) * 0.1f
    }
}