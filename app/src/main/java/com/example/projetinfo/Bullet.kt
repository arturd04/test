package com.example.projetinfo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Bullet(private var x: Float, private var y: Float) {
    private val speed = 15f

    val posY: Float
        get() = y

    fun update() {
        y -= speed
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply { color = Color.WHITE }
        canvas.drawRect(x - 5, y, x + 5, y + 20, paint)
    }

    fun intersects(alien: Alien): Boolean {
        val bulletRect = RectF(x - 5, y, x + 5, y + 20)
        return RectF.intersects(bulletRect, alien.getRect())
    }
}