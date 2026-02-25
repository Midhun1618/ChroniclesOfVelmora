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

    private lateinit var bgFar: Bitmap
    private lateinit var bgMid: Bitmap

    private val platforms = mutableListOf<Platform>()
    private lateinit var leftJoystick: Joystick

    private val mapWidth = 4330f
    private val mapHeight = 4200f

    init {
        holder.addCallback(this)

        player = Player(context, 600f, 3500f)
        camera = Camera(mapWidth, mapHeight)
        gameLoop = GameLoop(this, holder)

        // Load backgrounds
        bgFar = BitmapFactory.decodeResource(resources, R.drawable.bgfar)
        bgMid = BitmapFactory.decodeResource(resources, R.drawable.midbg)

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

        createArena(context)
    }

    private fun createArena(context: Context) {

        val floorHeight = 150f

        // FLOOR
        platforms.add(
            Platform(context, 0f, mapHeight - floorHeight, mapWidth, floorHeight, R.drawable.txtr_basetile)
        )

        // LEFT WALL
        platforms.add(
            Platform(context, 0f, 0f, 150f, mapHeight, R.drawable.txtr_basetile)
        )

        // RIGHT WALL
        platforms.add(
            Platform(context, mapWidth - 150f, 0f, 150f, mapHeight, R.drawable.txtr_basetile)
        )

        // MID LEVELS

        platforms.add(
            Platform(context, 500f, 3400f, 800f, 80f, R.drawable.txtr_basetile)
        )

        platforms.add(
            Platform(context, 2500f, 3400f, 800f, 80f, R.drawable.txtr_basetile)
        )

        platforms.add(
            Platform(context, 900f, 2600f, 600f, 80f, R.drawable.txtr_basetile)
        )

        platforms.add(
            Platform(context, 2200f, 2600f, 600f, 80f, R.drawable.txtr_basetile)
        )

        platforms.add(
            Platform(context, mapWidth / 2 - 400f, 1800f, 800f, 80f, R.drawable.txtr_basetile)
        )

        platforms.add(
            Platform(context, 700f, 1000f, 400f, 80f, R.drawable.txtr_basetile)
        )

        platforms.add(
            Platform(context, 2700f, 1000f, 400f, 80f, R.drawable.txtr_basetile)
        )
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val screenHeight = height.toFloat()

        leftJoystick = Joystick(
            250f,
            screenHeight - 250f,
            180f,
            80f
        )

        gameLoop.startLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.stopLoop()
    }

    fun update(deltaTime: Float) {

        val moveX = leftJoystick.getActuatorX()
        val moveY = leftJoystick.getActuatorY()

        player.update(deltaTime, platforms, moveX, moveY)
        camera.update(player)
    }
    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawColor(Color.BLACK)

        // Parallax
        canvas.drawBitmap(bgFar, -camera.cameraX * 0.3f, -camera.cameraY * 0.3f, null)
        canvas.drawBitmap(bgMid, -camera.cameraX * 0.6f, -camera.cameraY * 0.6f, null)

        // Platforms
        for (platform in platforms) {
            platform.draw(canvas, camera)
        }

        // Player
        player.draw(canvas, camera)

        // UI
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