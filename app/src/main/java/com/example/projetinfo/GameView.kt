package com.example.projetinfo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.MotionEvent
import android.content.Intent

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameThread: GameThread
    private val player: Player
    private val bullets = mutableListOf<Bullet>()
    private var lastBulletTime = System.currentTimeMillis()

    private val aliens = mutableListOf<Alien>()
    private var alienDirection = 5f
    private var shouldChangeDirection = false
    private val alienRows = 5
    private val aliensPerRow = 6
    private val alienSpacingX = 150f
    private val alienSpacingY = 150f
    private val offsetX = 100f
    private val offsetY = 100f

    private var isGameOver = false
    private var score = 0

    init {
        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        player = Player(context)
        spawnAlienGrid()
        isFocusable = true
    }

    private fun spawnAlienGrid() {
        aliens.clear()
        for (row in 0 until alienRows) {
            for (col in 0 until aliensPerRow) {
                val x = offsetX + col * alienSpacingX
                val y = offsetY + row * alienSpacingY
                aliens.add(Alien(context, x, y))
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread.running = true
        gameThread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread.running = false
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            player.x = event.x
        }
        return true
    }

    fun update() {
        if (isGameOver) return
        val currentTime = System.currentTimeMillis()

        // Tir
        if (currentTime - lastBulletTime > 1000) {
            bullets.add(Bullet(player.x, player.y))
            lastBulletTime = currentTime
        }

        bullets.forEach { it.update() }
        bullets.removeAll { it.posY < 0 }

        shouldChangeDirection = false

        // Vérifier les bords
        for (alien in aliens) {
            if ((alien.x + alien.width >= width && alienDirection > 0) ||
                (alien.x <= 0 && alienDirection < 0)) {
                shouldChangeDirection = true
                break
            }
        }

        // Déplacement
        if (shouldChangeDirection) {
            alienDirection *= -1
            aliens.forEach { it.y += 40f }

            // Ajouter une nouvelle ligne en haut
            val minY = aliens.minByOrNull { it.y }?.y ?: offsetY
            for (i in 0 until aliensPerRow) {
                val x = offsetX + i * alienSpacingX
                val y = minY - alienSpacingY
                aliens.add(Alien(context, x, y))
            }
        } else {
            aliens.forEach { it.x += alienDirection }
        }

        // Game over si un alien atteint le bas
        for (alien in aliens) {
            if (alien.y + alien.height >= height) {
                isGameOver = true
                gameThread.running = false
                val intent = Intent(context, GameOverActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        }

        // Collisions
        val bulletsToRemove = mutableListOf<Bullet>()
        val aliensToRemove = mutableListOf<Alien>()
        for (bullet in bullets) {
            for (alien in aliens) {
                if (bullet.intersects(alien)) {
                    bulletsToRemove.add(bullet)
                    aliensToRemove.add(alien)
                    score++
                }
            }
        }

        bullets.removeAll(bulletsToRemove)
        aliens.removeAll(aliensToRemove)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)
        player.draw(canvas)
        bullets.forEach { it.draw(canvas) }
        aliens.forEach { it.draw(canvas) }
    }
}
