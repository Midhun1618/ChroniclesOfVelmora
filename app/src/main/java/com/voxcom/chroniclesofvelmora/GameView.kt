package com.voxcom.chroniclesofvelmora

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.voxcom.chroniclesofvelmora.objects.Joystick
import com.voxcom.chroniclesofvelmora.objects.Platform
import com.voxcom.chroniclesofvelmora.objects.Player
import com.voxcom.chroniclesofvelmora.utils.Camera

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var gameLoop: GameLoop
    private lateinit var player: Player
    private lateinit var camera: Camera
    private lateinit var background: Bitmap

    private val platforms = mutableListOf<Platform>()

    private lateinit var leftJoystick: Joystick

    init {
        holder.addCallback(this)

        player = Player(context, 500f, 500f)
        camera = Camera()
        gameLoop = GameLoop(this, holder)

        background = BitmapFactory.decodeResource(resources, R.drawable.backdrop_basic)

        // Ground aligned to background
        val groundY = background.height - 100f
        platforms.add(Platform(0f, groundY, 3000f, 100f))

        // Floating platforms
        platforms.add(Platform(400f, 800f, 300f, 40f))
        platforms.add(Platform(900f, 650f, 300f, 40f))
        platforms.add(Platform(1400f, 500f, 300f, 40f))
        platforms.add(Platform(1900f, 700f, 300f, 40f))
        platforms.add(Platform(2400f, 600f, 300f, 40f))
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()

        leftJoystick = Joystick(
            200f,
            screenHeight - 200f,
            150f,
            70f
        )


        gameLoop.startLoop()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.stopLoop()
    }

    fun update(deltaTime: Float) {

        val moveX = leftJoystick.getActuatorX()
        val moveY = leftJoystick.getActuatorY()

        player.update(deltaTime, platforms, moveX, moveY)

        camera.update(player)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawColor(Color.BLACK)

        val bgX = -camera.cameraX
        val bgY = -camera.cameraY

        canvas.drawBitmap(background, bgX, bgY, null)

        // Platforms
        for (platform in platforms) {
            platform.draw(canvas, camera)
        }

        // Player
        player.draw(canvas, camera)

        // Draw joysticks ON TOP
        leftJoystick.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                leftJoystick.setActuator(event.x, event.y)
            }

            MotionEvent.ACTION_UP -> {
                leftJoystick.resetActuator()
            }
        }

        return true
    }
}