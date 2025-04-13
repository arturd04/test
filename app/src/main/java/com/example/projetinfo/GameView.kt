package com.example.projetinfo

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.max

open class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameLoopThread: GameLoopThread
    private val player: Player
    private var bullets = mutableListOf<Bullet>()
    private var enemyBullets = mutableListOf<EnemyBullet>()
    private var aliens = mutableListOf<Alien>()
    private var lastBulletTime = System.currentTimeMillis()
    private var lastEnemyShotTime = System.currentTimeMillis()

    // Paramètres de déplacement des aliens
    private var alienSpeed = 5f
    private var alienDirection = alienSpeed
    private val aliensPerRow = 6
    private val alienSpacingX = 150f
    private val alienSpacingY = 150f
    private val offsetX = 100f
    private val offsetY = 100f

    // Paramètres des balles tirées par le joueur
    private var bulletSpeed = 15f
    private var bulletInterval: Long = 1000L

    // Niveau courant et score
    private var currentLevel = 1
    private var levelScore = 0

    // Callback pour mettre à jour l'affichage des HP du joueur dans l'UI
    var onPlayerHpChanged: ((Int) -> Unit)? = null

    init {
        holder.addCallback(this)
        gameLoopThread = GameLoopThread(holder, this)
        player = Player(context)
        isFocusable = true
    }

    /**
     * Configure le niveau :
     * - La vitesse des aliens et des balles augmente avec le niveau.
     * - La cadence de tir augmente en diminuant l'intervalle entre les tirs.
     * Le nombre de lignes d'aliens reste fixe.
     */
    fun setLevel(level: Int) {
        currentLevel = level
        alienSpeed = 5f + (currentLevel - 1) * 1.5f
        alienDirection = alienSpeed
        bulletSpeed = 15f + (currentLevel - 1) * 5f
        bulletInterval = max(400L, 1000L - (currentLevel - 1) * 150L)
        spawnAlienGrid()
    }

    /**
     * Génère une grille d'aliens avec 3 lignes pour tous les niveaux.
     * Au niveau 1, tous les aliens sont basiques (1 hp).
     * À partir du niveau 2, un alien est choisi aléatoirement :
     *  - Alien basique (1 hp)
     *  - AlienWithHP avec 2 hp
     *  - AlienWithHP avec 3 hp.
     */
    private fun spawnAlienGrid() {
        aliens = mutableListOf()
        val numberOfRows = 3 // Nombre fixe de lignes
        for (row in 0 until numberOfRows) {
            for (col in 0 until aliensPerRow) {
                val x = offsetX + col * alienSpacingX
                val y = offsetY + row * alienSpacingY
                if (currentLevel == 1) {
                    aliens.add(Alien(context, x, y))
                } else {
                    val typeChoice = (1..3).random()
                    if (typeChoice == 1) {
                        aliens.add(Alien(context, x, y))
                    } else {
                        aliens.add(AlienWithHP(context, x, y, initialHp = typeChoice))
                    }
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoopThread.running = true
        gameLoopThread.start()
    }

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

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Pas d'action spécifique sur les changements de surface
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            player.x = event.x
        }
        return true
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        // Tir du joueur: créer une balle toutes les bulletInterval millisecondes
        if (currentTime - lastBulletTime > bulletInterval) {
            bullets.add(Bullet(player.x, player.y, bulletSpeed))
            lastBulletTime = currentTime
        }
        bullets.forEach { it.update() }
        bullets.removeAll { it.posY < 0 }

        // Tir des aliens: chance aléatoire pour tirer une balle ennemie
        if (currentTime - lastEnemyShotTime > 1500) {
            if (aliens.isNotEmpty() && (0..100).random() < 30) { // 30% de chance
                val shooter = aliens.random()
                enemyBullets.add(EnemyBullet(shooter.x + shooter.width / 2, shooter.y + shooter.height, bulletSpeed * 0.8f))
            }
            lastEnemyShotTime = currentTime
        }
        enemyBullets.forEach { it.update() }
        enemyBullets.removeAll { it.posY > height }

        // Mouvement des aliens: vérifier et inverser la direction s'ils touchent un bord
        var shouldChangeDirection = false
        for (alien in aliens) {
            if ((alien.x + alien.width >= width && alienDirection > 0) ||
                (alien.x <= 0 && alienDirection < 0)) {
                shouldChangeDirection = true
                break
            }
        }
        if (shouldChangeDirection) {
            alienDirection = if (alienDirection > 0) -alienSpeed else alienSpeed
            aliens.forEach { it.y += 40f }
        } else {
            aliens.forEach { it.x += alienDirection }
        }

        // Vérification de la position des aliens : si l'un d'eux dépasse la position du joueur,
        // cela signifie qu'il est passé derrière la zone de tir, donc game over.
        for (alien in aliens) {
            if (alien.y + alien.height >= player.y) {
                // Game over immédiat si un alien est trop bas
                val intent = Intent(context, GameOverActivity::class.java)
                intent.putExtra("score", levelScore)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                gameLoopThread.running = false
                return
            }
        }

        // Collision: balles du joueur vs aliens
        val bulletsToRemove = mutableListOf<Bullet>()
        val aliensToRemove = mutableListOf<Alien>()
        for (alien in aliens) {
            for (bullet in bullets) {
                if (bullet.intersects(alien.getRect())) {
                    bulletsToRemove.add(bullet)
                    if (alien is Damageable) {
                        if (alien.applyDamage(1)) {
                            aliensToRemove.add(alien)
                            levelScore++
                            ScoreManager.addScore(1)
                            break
                        }
                    } else {
                        aliensToRemove.add(alien)
                        levelScore++
                        ScoreManager.addScore(1)
                        break
                    }
                }
            }
        }
        bullets.removeAll(bulletsToRemove)
        aliens.removeAll(aliensToRemove)

        // Victoire : tous les aliens sont éliminés -> passage au niveau suivant
        if (aliens.isEmpty()) {
            val intent = Intent(context, LevelCompleteActivity::class.java)
            intent.putExtra("nextLevel", currentLevel + 1)
            intent.putExtra("score", levelScore)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            gameLoopThread.running = false
            return
        }

        // Défaite supplémentaire : si un alien atteint le bas (optional, peut être gardé pour redondance)
        for (alien in aliens) {
            if (alien.y + alien.height >= height) {
                val intent = Intent(context, GameOverActivity::class.java)
                intent.putExtra("score", levelScore)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                gameLoopThread.running = false
                return
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)
        player.draw(canvas)
        bullets.forEach { it.draw(canvas) }
        enemyBullets.forEach { it.draw(canvas) }
        aliens.forEach { it.draw(canvas) }
    }
}
