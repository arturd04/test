package com.example.projetinfo

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.max
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameLoopThread: GameLoopThread
    private val player: Player
    private var bullets = mutableListOf<Bullet>()
    private var enemyBullets = mutableListOf<EnemyBullet>()
    private var aliens = mutableListOf<Alien>()
    private var barriers = mutableListOf<Barrier>()
    private val barrierHitEffects = mutableListOf<BarrierHitEffect>()

    private var lastBulletTime = System.currentTimeMillis()
    private var alienShootInterval: Long = 10000L
    private var alienSpeed = 5f
    private var alienDirection = alienSpeed
    private val aliensPerRow = 6
    private val alienSpacingX = 150f
    private val alienSpacingY = 150f
    private val offsetX = 100f
    private val offsetY = 100f

    private var bulletSpeed = 80f
    private var bulletInterval: Long = 20L
    private var isShooting = false
    private var currentLevel = 1
    private var levelScore = 0

    private lateinit var heartBitmap: Bitmap

    var onPlayerHpChanged: ((Int) -> Unit)? = null

    init {
        heartBitmap = BitmapFactory.decodeResource(resources, R.drawable.heart)
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, 40, 40, true)

        holder.addCallback(this)
        gameLoopThread = GameLoopThread(holder, this)
        player = Player(context)
        isFocusable = true
    }

    private fun spawnBarriers() {
        barriers.clear()

        val columns = 4 // Nombre de colonnes de barrières
        val rows = 3    // Nombre de lignes
        val spacingX = width / (columns + 1)
        val spacingY = 100f // Espace vertical entre les lignes
        val baseY = height - 400f

        for (row in 0 until rows) {
            for (col in 1..columns) {
                val x = col * spacingX - 50f
                val y = baseY - (row * spacingY)
                barriers.add(Barrier(x, y))
            }
        }
    }


    fun setLevel(level: Int) {
        currentLevel = level
        alienSpeed = 2f + (level - 1) * 0.5f
        alienDirection = alienSpeed
        bulletSpeed = 15f + (level - 1) * 5f
        bulletInterval = max(400L, 1000L - (level - 1) * 150L)
        alienShootInterval = max(5000L, 10000L - (level - 1) * 2000L)
        spawnAlienGrid()
    }

    private fun spawnAlienGrid() {
        aliens = mutableListOf()
        for (row in 0 until 3) {
            for (col in 0 until aliensPerRow) {
                val x = offsetX + col * alienSpacingX
                val y = offsetY + row * alienSpacingY
                val alien = AlienFactory.createAlien(context, x, y, currentLevel)
                aliens.add(alien)

            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        spawnBarriers()
        gameLoopThread.running = true
        gameLoopThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameLoopThread.running = false
        while (retry) {
            try {
                gameLoopThread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                player.setTarget(event.x)
                isShooting = true
            }
            MotionEvent.ACTION_UP -> {
                isShooting = false
            }
        }
        return true
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        player.update()

        // Tir automatique du joueur si le doigt est maintenu
        if (isShooting && currentTime - lastBulletTime > bulletInterval) {
            bullets.add(Bullet(player.x, player.y, bulletSpeed))
            lastBulletTime = currentTime
        }
        bullets.forEach { it.update() }
        bullets.removeAll { it.y < 0 }

        // Tir indépendant et aléatoire des aliens
        for (alien in aliens) {
            if (currentTime - alien.lastShotTime >= alien.nextShotDelay) {
                enemyBullets.add(
                    EnemyBullet(
                        alien.x + alien.width / 2,
                        alien.y + alien.height,
                        bulletSpeed * 0.8f
                    )
                )
                alien.lastShotTime = currentTime
                alien.nextShotDelay = (2000..10000).random().toLong() // nouveau délai aléatoire
            }
        }

        // Mise à jour des tirs ennemis
        enemyBullets.forEach { it.update() }
        enemyBullets.removeAll { it.y > height }

        // Gestion des collisions tirs ennemis ↔ barrières + effets visuels
        val barrierHits = mutableListOf<Pair<Barrier, EnemyBullet>>()
        for (bullet in enemyBullets) {
            for (barrier in barriers) {
                if (RectF.intersects(bullet.getRect(), barrier.getRect())) {
                    barrierHits.add(barrier to bullet)
                }
            }
        }
        for ((barrier, bullet) in barrierHits) {
            barrier.hit()
            enemyBullets.remove(bullet)
            barrierHitEffects.add(BarrierHitEffect(bullet.x, bullet.y))
        }
        barriers.removeAll { it.isDestroyed() }

        // Limite de descente des aliens + changement de direction
        val maxAlienY = height - 500f
        var shouldChangeDirection = false
        for (alien in aliens) {
            if ((alien.x + alien.width >= width && alienDirection > 0) ||
                (alien.x <= 0 && alienDirection < 0)
            ) {
                shouldChangeDirection = true
                break
            }
        }

        if (shouldChangeDirection) {
            alienDirection = -alienDirection
            for (alien in aliens) {
                if (alien.y + alien.height < maxAlienY) {
                    alien.y += 40f
                }
            }
        }

        // Vitesse ajustée selon le nombre de lignes restantes
        val remainingRows = aliens.map { it.y }.distinct().size
        val adjustedSpeed = alienSpeed * (1f + ((3 - remainingRows) * 0.3f))
        val actualDirection = if (alienDirection > 0) adjustedSpeed else -adjustedSpeed
        aliens.forEach { it.x += actualDirection }

        // Game Over si un alien atteint le joueur
        for (alien in aliens) {
            if (alien.y + alien.height >= player.y) {
                val intent = Intent(context, GameOverActivity::class.java)
                intent.putExtra("score", levelScore)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                gameLoopThread.running = false
                return
            }
        }

        // Collision tirs ennemis ↔ joueur
        val enemyHits = mutableListOf<EnemyBullet>()
        for (bullet in enemyBullets) {
            if (RectF.intersects(bullet.getRect(), player.getRect())) {
                enemyHits.add(bullet)
                player.hp--
                onPlayerHpChanged?.invoke(player.hp)
                if (player.hp <= 0) {
                    val intent = Intent(context, GameOverActivity::class.java)
                    intent.putExtra("score", levelScore)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    gameLoopThread.running = false
                    return
                }
            }
        }
        enemyBullets.removeAll(enemyHits)

        // Collision tirs joueur ↔ aliens
        val bulletsToRemove = mutableListOf<Bullet>()
        val aliensToRemove = mutableListOf<Alien>()
        for (alien in aliens) {
            for (bullet in bullets) {
                if (RectF.intersects(bullet.getRect(), alien.getRect())) {
                    bulletsToRemove.add(bullet)
                    if (alien is Damageable && alien.applyDamage(1)) {
                        aliensToRemove.add(alien)
                    } else if (alien !is Damageable) {
                        aliensToRemove.add(alien)
                    }
                    levelScore++
                    ScoreManager.addScore(1)
                    break
                }
            }
        }
        bullets.removeAll(bulletsToRemove)
        aliens.removeAll(aliensToRemove)

        // Niveau terminé
        if (aliens.isEmpty()) {
            val intent = Intent(context, LevelCompleteActivity::class.java)
            intent.putExtra("nextLevel", currentLevel + 1)
            intent.putExtra("score", levelScore)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            gameLoopThread.running = false
        }

        // Mise à jour des effets visuels des impacts
        barrierHitEffects.forEach { it.update() }
        barrierHitEffects.removeAll { it.isFinished() }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)
        player.draw(canvas)
        bullets.forEach { it.draw(canvas) }
        enemyBullets.forEach { it.draw(canvas) }
        aliens.forEach { it.draw(canvas) }
        barriers.forEach { it.draw(canvas, Paint()) }
        barrierHitEffects.forEach { it.draw(canvas, Paint()) }

        // Affichage des vies avec des cœurs rouges
        val spacing = 50f
        val totalWidth = (player.hp - 1) * spacing
        val startX = player.x - totalWidth / 2
        val y = player.y + player.height + 20f

        for (i in 0 until player.hp) {
            val x = startX + i * spacing
            canvas.drawBitmap(heartBitmap, x, y, null)
        }

    }
}
