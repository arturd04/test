package com.example.projetinfo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class BarrierHitEffect(var x: Float, var y: Float) {
    private var life = 5 // Frames restantes pour l'effet

    fun update() {
        life--
    }

    fun draw(canvas: Canvas, paint: Paint) {
        if (life > 0) {
            paint.color = Color.WHITE
            canvas.drawCircle(x, y, 20f, paint)
        }
    }

    fun isFinished(): Boolean = life <= 0
}
