package com.voxcom.chroniclesofvelmora

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.voxcom.chroniclesofvelmora.objects.Enemy
import com.voxcom.chroniclesofvelmora.objects.EnemyBullet
import com.voxcom.chroniclesofvelmora.objects.ExplosionParticle
import com.voxcom.chroniclesofvelmora.objects.Joystick
import com.voxcom.chroniclesofvelmora.objects.Platform
import com.voxcom.chroniclesofvelmora.objects.Player
import com.voxcom.chroniclesofvelmora.objects.Strike
import com.voxcom.chroniclesofvelmora.utils.Camera
import com.voxcom.chroniclesofvelmora.utils.SoundManager

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var gameLoop: GameLoop
    private lateinit var player: Player
    private lateinit var camera: Camera

    private lateinit var bgFar: Bitmap
    private lateinit var bgMid: Bitmap
    private lateinit var soundManager: SoundManager
    private var musicPlayer: MediaPlayer? = null
    private var spawnTimer = 0f
    private val spawnInterval = 15f
    private var jetpackSoundPlaying = false

    private val enemies = mutableListOf<Enemy>()
    private val strike = mutableListOf<Strike>()
    private val platforms = mutableListOf<Platform>()
    private val particles = mutableListOf<ExplosionParticle>()
    private lateinit var leftJoystick: Joystick
    private val enemyBullets = mutableListOf<EnemyBullet>()
    private fun spawnEnemy() {

        val spawnX = if ((0..1).random() == 0)
            300f
        else
            mapWidth - 300f

        val spawnY = mapHeight - 500f

        enemies.add(
            Enemy(context, spawnX, spawnY)
        )
    }

    private val mapWidth = 6330f
    private val mapHeight = 4200f
    private var maxAmmo = 3
    private var currentAmmo = 3

    private var reloadTime = 1.5f
    private var reloadTimer = 0f
    private var isReloading = false

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

        val wallThickness = 100f
        val floorThickness = 150f
        val platformThickness = 50f

        // ================= OUTER BORDERS =================

        // FLOOR
        platforms.add(
            Platform(
                context,
                0f,
                mapHeight - floorThickness,
                mapWidth,
                floorThickness,
                R.drawable.txtr_basetile
            )
        )

        // CEILING
        platforms.add(
            Platform(
                context,
                0f,
                0f,
                mapWidth,
                wallThickness,
                R.drawable.txtr_woodenfloor
            )
        )

        // LEFT WALL
        platforms.add(
            Platform(
                context,
                0f,
                0f,
                wallThickness,
                mapHeight,
                R.drawable.texture
            )
        )

        // RIGHT WALL
        platforms.add(
            Platform(
                context,
                mapWidth - wallThickness,
                0f,
                wallThickness,
                mapHeight,
                R.drawable.texture
            )
        )

        // ================= SAMPLE PLATFORM =================

        platforms.add(
            // platform10
            Platform(
                context,
                100f,     // X
                mapHeight - floorThickness-400f,
                300f,      // width
                platformThickness,       // height
                R.drawable.texture
            )
        )
        platforms.add(
            // platform11
            Platform(
                context,
                700f,
                mapHeight - floorThickness-400f,
                800f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform12
            Platform(
                context,
                100f,
                mapHeight - floorThickness-800f,
                700f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform13
            Platform(
                context,
                1100f,
                mapHeight - floorThickness-800f,
                400f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform14
            Platform(
                context,
                100f,
                mapHeight - floorThickness-1200f,
                1100f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform15
            Platform(
                context,
                100f,
                mapHeight - floorThickness-1600f,
                400f,
                platformThickness,
                R.drawable.texture
            )
        )

        platforms.add(
            Platform(
                context,
                1500f,
                mapHeight - floorThickness-1200f,
                platformThickness,
                900f,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform9
            Platform(
                context,
                2100f,
                mapHeight - floorThickness-500f,
                500f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform8
            Platform(
                context,
                2400f,
                mapHeight - floorThickness-1200f,
                200f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform7
            Platform(
                context,
                2600f,
                mapHeight - floorThickness-1700f,
                400f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall
            Platform(
                context,
                2600f,
                mapHeight - floorThickness-1650f,
                400f,
                1700f,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall1
            Platform(
                context,
                mapWidth-wallThickness-900,
                mapHeight - floorThickness-2000f,
                platformThickness,
                1700f,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall2
            Platform(
                context,
                mapWidth-wallThickness-2300f,
                mapHeight - floorThickness-2000f,
                platformThickness,
                2000f,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform6
            Platform(
                context,
                mapWidth-wallThickness-2300f,
                mapHeight - floorThickness-2000f,
                1400f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall3
            Platform(
                context,
                mapWidth-wallThickness-1900f,
                mapHeight - floorThickness-1600f,
                platformThickness,
                500f,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall4
            Platform(
                context,
                mapWidth-wallThickness-1600f,
                mapHeight - floorThickness-1600f,
                platformThickness,
                500f,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall5
            Platform(
                context,
                mapWidth-wallThickness-1300f,
                mapHeight - floorThickness-1600f,
                platformThickness,
                800f,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform4
            Platform(
                context,
                mapWidth-wallThickness-1500f,
                mapHeight - floorThickness-1600f,
                300f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform3
            Platform(
                context,
                mapWidth-wallThickness-1900f,
                mapHeight - floorThickness-1100f,
                300f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            // platform1
            Platform(
                context,
                mapWidth-wallThickness-1800f,
                mapHeight - floorThickness-400f,
                900f,
                platformThickness,
                R.drawable.texture
            )
        )
        platforms.add(
            //wall3
            Platform(
                context,
                mapWidth-wallThickness-1900f,
                mapHeight - floorThickness-800f,
                platformThickness,
                400f,
                R.drawable.texture
            )
        )
        platforms.add(
            //platform2
            Platform(
                context,
                mapWidth-wallThickness-1900f,
                mapHeight - floorThickness-800f,
                700f,
                platformThickness,
                R.drawable.texture
            )
        )
        enemies.add(Enemy(context, 1200f, 200f))
        enemies.add(Enemy(context, 2700f, mapHeight - floorThickness-1800f))
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        soundManager = SoundManager(context)
        val screenHeight = height.toFloat()

        leftJoystick = Joystick(
            250f,
            screenHeight - 250f,
            180f,
            80f
        )
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(context, R.raw.bgm)
            musicPlayer?.isLooping = true
            musicPlayer?.setVolume(0.2f, 0.2f)
            musicPlayer?.start()
        }
        gameLoop.startLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop.stopLoop()
        soundManager.release()
        musicPlayer?.release()
        musicPlayer = null

    }

    fun update(deltaTime: Float) {

        val moveX = leftJoystick.getActuatorX()
        val moveY = leftJoystick.getActuatorY()

        player.update(deltaTime, platforms, moveX, moveY)
        camera.update(player)
        val particleIterator = particles.iterator()

        while (particleIterator.hasNext()) {
            val p = particleIterator.next()
            p.update(deltaTime)

            if (p.isDead) {
                particleIterator.remove()
            }
        }
        if (!player.isDead()) {

            spawnTimer += deltaTime

            if (spawnTimer >= spawnInterval) {
                spawnEnemy()
                spawnTimer = 0f
            }
        }

        val iterator = strike.iterator()
        while (iterator.hasNext()) {
            val strike = iterator.next()
            strike.update(deltaTime)

            // Remove if hits platform
            for (platform in platforms) {
                if (strike.isCollidingWithPlatform(platform)) {
                    strike.isActive = false
                }
            }

            // Remove enemy if hit
            for (enemy in enemies) {
                if (strike.isCollidingWithEnemy(enemy)) {
                    enemy.kill()
                    soundManager.playExplosion()

                    repeat(30) {
                        particles.add(
                            ExplosionParticle(
                                enemy.worldX + 50f,
                                enemy.worldY + 120f
                            )
                        )
                    }
                    strike.isActive = false
                    break
                }
            }

            if (!strike.isActive) {
                iterator.remove()
            }
        }
        // Reload logic
        if (isReloading) {
            reloadTimer += deltaTime
            if (reloadTimer >= reloadTime) {
                currentAmmo = maxAmmo
                isReloading = false
            }
        }
        val bulletIterator = enemyBullets.iterator()

        while (bulletIterator.hasNext()) {

            val bullet = bulletIterator.next()
            bullet.update(deltaTime)

            // Hit player
            if (bullet.isCollidingWithPlayer(player)) {
                player.takeDamage(10)
                soundManager.playHit()
                bulletIterator.remove()
                continue
            }

            // Hit platform
            for (platform in platforms) {
                if (bullet.isCollidingWithPlatform(platform)) {
                    bulletIterator.remove()
                    break
                }
            }
        }
        val enemyIterator = enemies.iterator()

        while (enemyIterator.hasNext()) {

            val enemy = enemyIterator.next()
            enemy.update(deltaTime, platforms, player) { x, y, dir ->
                enemyBullets.add(
                    EnemyBullet(context, x, y, dir)
                )
            }

            if (enemy.isRemoved) {
                enemyIterator.remove()
            }
        }
        if (player.isJetpackActive()) {

            if (!jetpackSoundPlaying) {
                soundManager.playJetpack()
                jetpackSoundPlaying = true
            }

        } else {
            jetpackSoundPlaying = false
        }
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
        for (enemy in enemies) {
            enemy.draw(canvas, camera)
        }
        for (strike in strike) {
            strike.draw(canvas, camera)
        }

        // Player
        player.draw(canvas, camera)

        // UI
        leftJoystick.draw(canvas)
        // ================= LIFE BAR =================
        val barWidth = 400f
        val barHeight = 30f
        val healthRatio = player.currentHealth.toFloat() / player.maxHealth

        val paintBg = android.graphics.Paint().apply {
            color = android.graphics.Color.DKGRAY
        }

        val paintHp = android.graphics.Paint().apply {
            color = android.graphics.Color.RED
        }
        for (bullet in enemyBullets) {
            bullet.draw(canvas, camera)
        }

// Background
        canvas.drawRect(50f, 50f, 50f + barWidth, 50f + barHeight, paintBg)

// Health fill
        canvas.drawRect(
            50f,
            50f,
            50f + barWidth * healthRatio,
            50f + barHeight,
            paintHp
        )
        // ================= AMMO DISPLAY =================
        val ammoPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.YELLOW
            textSize = 50f
        }

        canvas.drawText(
            "Ammo: $currentAmmo",
            50f,
            130f,
            ammoPaint
        )

        if (isReloading) {
            canvas.drawText(
                "Reloading...",
                50f,
                190f,
                ammoPaint
            )
        }
        for (p in particles) {
            p.draw(canvas, camera)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                if (event.x > width / 2 && !player.isDead()) {

                    if (currentAmmo > 0 && !isReloading) {

                        strike.add(

                            Strike(
                                context,
                                player.worldX + 60f,
                                player.worldY + 60f,
                                player.getFacingDirection()
                            )
                        )
                        soundManager.playShoot()

                        currentAmmo--

                        if (currentAmmo == 0) {
                            isReloading = true
                            soundManager.playReload()
                            reloadTimer = 0f
                        }
                    }
                }
            }

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