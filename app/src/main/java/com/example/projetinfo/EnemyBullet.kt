package com.example.projetinfo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class EnemyBullet(var x: Float, var y: Float, private val speed: Float = 5f) {
    private val radius = 10f
    private val paint = Paint().apply { color = Color.RED }

    fun update() {
        y += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }

    fun getRect(): RectF {
        return RectF(x - radius, y - radius, x + radius, y + radius)
    }
}

