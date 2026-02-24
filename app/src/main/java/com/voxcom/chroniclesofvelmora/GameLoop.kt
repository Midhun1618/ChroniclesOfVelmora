package com.voxcom.chroniclesofvelmora

import android.view.SurfaceHolder

class GameLoop(
    private val gameView: GameView,
    private val surfaceHolder: SurfaceHolder
) : Thread() {

    private var running = false

    fun startLoop() {
        running = true
        start()
    }

    fun stopLoop() {
        running = false
    }

    override fun run() {

        var lastTime = System.nanoTime()

        while (running) {

            val now = System.nanoTime()
            val deltaTime = (now - lastTime) / 1_000_000_000f
            lastTime = now

            val canvas = surfaceHolder.lockCanvas()

            if (canvas != null) {
                synchronized(surfaceHolder) {
                    gameView.update(deltaTime)
                    gameView.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
}