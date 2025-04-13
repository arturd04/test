package com.example.projetinfo

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameLoopThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {

    var running = false

    override fun run() {
        var canvas: Canvas? = null
        while (running) {
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    if (canvas != null) {
                        gameView.draw(canvas)
                    }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }
        }
    }
}
