package com.example.projetinfo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class EnemyBullet(var posX: Float, var posY: Float, private val speed: Float = 5f) {
    private val radius = 10f
    private val paint = Paint().apply { color = Color.RED }

    fun update() {
        posY += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(posX, posY, radius, paint)
    }

    fun intersects(rect: RectF): Boolean {
        return posX + radius > rect.left &&
                posX - radius < rect.right &&
                posY + radius > rect.top &&
                posY - radius < rect.bottom
    }
}
