package com.voxcom.chroniclesofvelmora

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.voxcom.chroniclesofvelmora.objects.Player
import com.voxcom.chroniclesofvelmora.utils.Camera

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var gameLoop: GameLoop
    private lateinit var player: Player
    private lateinit var camera: Camera
    private lateinit var background: Bitmap

    init {
        holder.addCallback(this)
        player = Player(context,500f, 500f)
        camera = Camera()
        gameLoop = GameLoop(this, holder)
        background = BitmapFactory.decodeResource(resources, R.drawable.backdrop_basic)
    }
    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ){

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoop.startLoop()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.stopLoop()
    }

    fun update() {
        player.update()
        camera.update(player)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawColor(Color.BLACK)

        val bgX = -camera.cameraX
        val bgY = -camera.cameraY

        canvas.drawBitmap(background, bgX, bgY, null)

        player.draw(canvas, camera)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val screenWidth = width
        val screenHeight = height

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                // Jump if tapped upper half
                if (event.y < screenHeight / 2) {
                    player.jump()
                } else {
                    if (event.x < screenWidth / 2) {
                        player.moveLeft = true
                        player.moveRight = false
                    } else {
                        player.moveRight = true
                        player.moveLeft = false
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                player.moveLeft = false
                player.moveRight = false
            }
        }

        return true
    }
}