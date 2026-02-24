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
        while (running) {
            val canvas = surfaceHolder.lockCanvas()
            if (canvas != null) {
                synchronized(surfaceHolder) {
                    gameView.update()
                    gameView.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            sleep(16) // ~60 FPS
        }
    }
}