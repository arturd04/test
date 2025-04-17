package com.example.projetinfo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Barrier(val x: Float, val y: Float, val width: Float = 100f, val height: Float = 40f) {
    var hp: Int = 3

    fun draw(canvas: Canvas, paint: Paint) {
        when (hp) {
            3 -> paint.color = Color.GREEN
            2 -> paint.color = Color.YELLOW
            1 -> paint.color = Color.RED
        }
        canvas.drawRect(x, y, x + width, y + height, paint)
    }

    fun getRect(): RectF {
        return RectF(x, y, x + width, y + height)
    }

    fun hit() {
        hp--
    }

    fun isDestroyed(): Boolean = hp <= 0
}
