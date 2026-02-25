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
    private lateinit var bgFar: Bitmap
    private lateinit var bgMid: Bitmap
    private val platforms = mutableListOf<Platform>()
    private lateinit var leftJoystick: Joystick
    private val mapWidth = 3000f
    private val mapHeight = 1800f

    init {
        holder.addCallback(this)

        player = Player(context, 500f, 500f)
        camera = Camera()
        gameLoop = GameLoop(this, holder)

        background = BitmapFactory.decodeResource(resources, R.drawable.backdrop_basic)
        bgFar = BitmapFactory.decodeResource(resources, R.drawable.bgfar)
        bgMid = BitmapFactory.decodeResource(resources, R.drawable.midbg)

        background = Bitmap.createScaledBitmap(
            background,
            mapWidth.toInt(),
            mapHeight.toInt(),
            false
        )
        bgFar = Bitmap.createScaledBitmap(
            bgFar,
            mapWidth.toInt(),
            mapHeight.toInt(),
            false
        )

        bgMid = Bitmap.createScaledBitmap(
            bgMid,
            mapWidth.toInt(),
            mapHeight.toInt(),
            false
        )
        platforms.add(
            Platform(
                context,
                0f,
                mapHeight - 100f,
                mapWidth,
                100f,
                R.drawable.txtr_basetile
            )
        )

// Left wall
        platforms.add(
            Platform(
                context,
                0f,
                0f,
                100f,
                mapHeight,
                R.drawable.grey_brick
            )
        )

// Floating platform
        platforms.add(
            Platform(
                context,
                1300f,
                850f,
                400f,
                60f,
                R.drawable.brown_brick
            )
        )}

    override fun surfaceCreated(holder: SurfaceHolder) {
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

        // Parallax offsets
        val farX = -camera.cameraX * 0.3f
        val farY = -camera.cameraY * 0.3f

        val midX = -camera.cameraX * 0.6f
        val midY = -camera.cameraY * 0.6f

        // Draw far layer
        canvas.drawBitmap(bgFar, farX, farY, null)

        // Draw mid layer
        canvas.drawBitmap(bgMid, midX, midY, null)

        // Platforms
        for (platform in platforms) {
            platform.draw(canvas, camera)
        }

        // Player
        player.draw(canvas, camera)

        // Joystick
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