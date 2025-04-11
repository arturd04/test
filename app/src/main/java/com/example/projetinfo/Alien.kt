package com.example.projetinfo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

class Alien(context: Context, var x: Float, var y: Float) {
    private val bitmap: Bitmap
    val width: Float
    val height: Float

    init {
        val original = BitmapFactory.decodeResource(context.resources, R.drawable.alien)
        bitmap = Bitmap.createScaledBitmap(original, 100, 100, true)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }

    fun getRect(): RectF {
        return RectF(x, y, x + width, y + height)
    }
}